package com.example.workout.presentation.ui.workoutdetails

import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.MappingTrackSelector
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.navArgs
import com.example.workout.MainActivity
import com.example.workout.R
import com.example.workout.databinding.FragmentWorkoutDetailsBinding
import com.example.workout.domain.interactors.workouts.Workout
import com.example.workout.presentation.ViewModels
import com.example.workout.presentation.ui.BaseConfiguration
import com.example.workout.utils.addMinutes
import com.example.workout.utils.dpToPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

@UnstableApi
class WorkoutDetails : Fragment() {

    private var _binding: FragmentWorkoutDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutDetailsViewModel by viewModels {
        ViewModels.factory
    }

    private val args: WorkoutDetailsArgs by navArgs()

    private var defaultTrackSelector: DefaultTrackSelector? = null
    private lateinit var playerView: PlayerView
    private var player: ExoPlayer? = null
    private lateinit var dataSourceFactory: DataSource.Factory

    private lateinit var fullButton: ImageView
    private lateinit var tenLeftButton: ImageView
    private lateinit var tenRightButton: ImageView
    private lateinit var playPauseButton: ImageView
    private lateinit var speedButton: LinearLayout
    private lateinit var speedText: TextView
    private lateinit var qualityButton: LinearLayout
    private lateinit var qualityText: TextView

    private var speedSelectedIndex = 2

    private lateinit var videoQualities: List<String>
    private var qualitySelectedIndex = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userAgent = context?.let { Util.getUserAgent(it, "Workout/1.0") }

        dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(userAgent)

        context?.let { context ->


            defaultTrackSelector =
                DefaultTrackSelector(context, AdaptiveTrackSelection.Factory())

            player = defaultTrackSelector?.let {
                ExoPlayer.Builder(context)
                    .setTrackSelector(it)
            }?.build()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setWorkout(workout = args.workout)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect() {
                updateView(it)
            }
        }
        initPlayerUI()

        binding.loadingErrorBlock.btnRetry.setOnClickListener {
            viewModel.retry()
        }
    }

    override fun onResume() {
        player?.play()
        super.onResume()
    }

    override fun onPause() {
        player?.pause()
        super.onPause()
    }

    override fun onStop() {
        player?.release()
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    private fun initPlayerUI() {
        playerView = binding.playerView
        playerView.player = player

        fullButton = playerView.findViewById(R.id.full_button)
        tenLeftButton = playerView.findViewById(R.id.ten_left_button)
        tenRightButton = playerView.findViewById(R.id.ten_right_button)
        playPauseButton = playerView.findViewById(R.id.exo_play_pause)
        speedButton = playerView.findViewById(R.id.playback_speed_container)
        speedText = playerView.findViewById(R.id.speed_text)
        qualityButton = playerView.findViewById(R.id.quality_container)
        qualityText = playerView.findViewById(R.id.quality_text)


        tenLeftButton.setOnClickListener {
            player?.let {
                val num = it.contentPosition - TEN_SEC
                if (num < 0) {
                    it.seekTo(0)
                } else {
                    it.seekTo(it.contentPosition - TEN_SEC)
                }
            }
        }

        tenRightButton.setOnClickListener { player?.let { it.seekTo(it.contentPosition + TEN_SEC) } }

        fullButton.setOnClickListener {
            if (viewModel.state.value.isFullscreen) {
                exitFullscreen()
                viewModel.updateFullscreenState(false)
            } else {
                enterFullscreen()
                viewModel.updateFullscreenState(true)
            }
        }

        speedButton.setOnClickListener { showSpeedDialog() }
        qualityButton.setOnClickListener { showQualityChangeDialog() }
    }

    private fun updateView(state: WorkoutDetailsState) {
        when (state.configuration) {
            BaseConfiguration.INITIAL -> {}
            BaseConfiguration.LOADING -> showLoading()

            BaseConfiguration.READY -> showWorkoutDetails(state.workout, state.link)

            BaseConfiguration.ERROR ->
                showError(state.error ?: getString(R.string.unknown_error))
        }
    }

    private fun showLoading() {
        hideAll()
        binding.loadingErrorBlock.container.isVisible = true
        binding.loadingErrorBlock.loadingContainer.isVisible = true
    }

    private fun showError(message: String) {
        hideAll()
        binding.loadingErrorBlock.container.isVisible = true
        binding.loadingErrorBlock.errorContainer.isVisible = true
        binding.loadingErrorBlock.tvErrorMessage.text = message
    }

    private fun showWorkoutDetails(workout: Workout?, link: String) {
        hideAll()

        val mediaSource = buildMediaSource(Uri.parse(link))

        player?.setMediaSource(mediaSource)
        player?.prepare()

        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        binding.playerProgressBar.isVisible = false
                        playPauseButton.isVisible = true
                        videoQualities = getVideoQualitiesTracks()
                    }
                    Player.STATE_BUFFERING -> {
                        binding.playerProgressBar.isVisible = true
                        playPauseButton.isVisible = false
                    }
                    else -> {
                        binding.playerProgressBar.isVisible = false
                        playPauseButton.isVisible = true
                    }
                }

                super.onPlaybackStateChanged(playbackState)
            }
        })

        binding.workoutDetails.isVisible = true
        binding.workoutTitle.text = workout?.title
        binding.workoutDescription.text = workout?.description
        binding.workoutType.text = workout?.type?.text
        binding.workoutDuration.text = workout?.duration?.addMinutes()
    }

    private fun hideAll() {
        binding.workoutDetails.isVisible = false
        binding.loadingErrorBlock.container.isVisible = false
        binding.loadingErrorBlock.loadingContainer.isVisible = false
        binding.loadingErrorBlock.errorContainer.isVisible = false
        binding.loadingErrorBlock.emptyContainer.isVisible = false
    }

    private fun showSpeedDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Скорость видео")
                .setSingleChoiceItems(speeds, speedSelectedIndex, { _, index ->
                    speedSelectedIndex = index
                })
                .setPositiveButton("Принять", { _, _ ->
                    viewModel.setSpeedIndex(speedSelectedIndex)
                    val speed = speedValues.getOrNull(speedSelectedIndex)
                    speed?.let { player?.setPlaybackSpeed(speed) }
                    speedText.text = speeds.getOrNull(speedSelectedIndex)
                })
                .setNegativeButton("Вернуться", { _, _ ->
                    speedSelectedIndex = viewModel.state.value.selectedSpeedIndex
                })
                .show()
        }
    }

    private fun showQualityChangeDialog() {
        if (::videoQualities.isInitialized) {
            if (videoQualities.isNotEmpty()) {
                getQualityChooseDialog()
            } else {
                Toast.makeText(context, "Нет другого качества", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Подождите ...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getQualityChooseDialog() {
        context?.let {
            val qualities = mutableListOf<String>()
            qualities.add("Auto")
            qualities.addAll(videoQualities)

            MaterialAlertDialogBuilder(it)
                .setTitle("Качество видео")
                .setSingleChoiceItems(qualities.toTypedArray(), qualitySelectedIndex, { _, index ->
                    qualitySelectedIndex = index
                })
                .setPositiveButton("Принять", { _, _ ->
                    viewModel.setQualityIndex(qualitySelectedIndex)
                    if (qualitySelectedIndex == 0) {
                        defaultTrackSelector?.let {
                            it.setParameters(it.buildUponParameters().setMaxVideoSizeSd())
                        }
                        qualityText.text = "Auto"
                    } else {
                        val qualityInfo = videoQualities.get(qualitySelectedIndex - 1).split("x")

                        val videoWidth = Integer.parseInt(qualityInfo[0])
                        val videoHeight = Integer.parseInt(qualityInfo[1])
                        defaultTrackSelector?.let {
                            it.setParameters(
                                it.buildUponParameters().setMaxVideoSize(videoWidth, videoHeight)
                                    .setMinVideoSize(videoWidth, videoHeight)
                            )
                        }
                        qualityText.text = qualityInfo[1] + "p"
                    }
                })
                .setNegativeButton("Вернуться", { _, _ ->
                    qualitySelectedIndex = viewModel.state.value.selectedQualityIndex
                })
                .show()
        }
    }

    private fun exitFullscreen() {
        (activity as? MainActivity)?.showToolbar()

        val params = playerView.layoutParams as ConstraintLayout.LayoutParams
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = 300.dpToPx(resources.displayMetrics)
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        playerView.setPadding(0, 0, 0, 0)
        playerView.layoutParams = params

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }

    private fun enterFullscreen() {
        (activity as? MainActivity)?.hideToolbar()

        val params = playerView.layoutParams as ConstraintLayout.LayoutParams
        params.topToTop = ConstraintLayout.LayoutParams.UNSET
        params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
        params.startToStart = ConstraintLayout.LayoutParams.UNSET
        params.endToEnd = ConstraintLayout.LayoutParams.UNSET
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        playerView.setPadding(0, 0, 0, 0)
        playerView.layoutParams = params

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun getVideoQualitiesTracks(): List<String> {
        val qualities = mutableListOf<String>()

        val renderTrack = defaultTrackSelector?.currentMappedTrackInfo

        renderTrack?.let {
            val renderCount = it.rendererCount

            for (index in 0 until renderCount) {
                val trackGroups = renderTrack.getTrackGroups(index)
                val trackGroupsCount = trackGroups.length
                if (trackGroupsCount != 0) {
                    val trackGroupType = renderTrack.getRendererType(index)
                    if (trackGroupType == C.TRACK_TYPE_VIDEO) {
                        for (groupIndex in 0 until trackGroupsCount) {
                            val videoQualityTrackCount = trackGroups.get(groupIndex).length
                            for (trackIndex in 0 until videoQualityTrackCount) {
                                val isTrackSupport = renderTrack.getTrackSupport(
                                    index,
                                    groupIndex,
                                    trackIndex
                                ) == C.FORMAT_HANDLED
                                if (isTrackSupport) {
                                    val trackGroup = trackGroups.get(groupIndex)

                                    val videoWidth = trackGroup.getFormat(trackIndex).width
                                    val videoHeight = trackGroup.getFormat(trackIndex).height

                                    val quality = videoWidth.toString() + "x" + videoHeight
                                    qualities.add(quality)
                                }
                            }
                        }
                    }
                }
            }
        }
        return qualities
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val contentType = Util.inferContentType(uri)
        val mediaItem = MediaItem.fromUri(uri)

        return if (contentType == C.CONTENT_TYPE_HLS) {
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        } else if (contentType == C.CONTENT_TYPE_DASH) {
            DashMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        } else if (contentType == C.CONTENT_TYPE_SS) {
            SsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        } else if (contentType == C.CONTENT_TYPE_RTSP) {
            RtspMediaSource.Factory().createMediaSource(mediaItem)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        }
    }

    companion object {
        private val speeds = arrayOf("0.5x", "0.75x", "1x", "1.5x", "2x")
        private val speedValues = arrayOf(0.5f, 0.75f, 1f, 1.5f, 2f)

        private const val TEN_SEC = 10000

        fun newInstance() = WorkoutDetails()
    }
}
