package com.example.workout.domain.repositories.workout

import com.example.workout.domain.common.BaseError
import com.example.workout.domain.common.Response
import com.example.workout.domain.models.workout.WorkoutDetailsDto
import com.example.workout.domain.models.workout.WorkoutDto
import com.example.workout.domain.models.workout.WorkoutType
import kotlinx.coroutines.delay
import kotlin.random.Random

class WorkoutRepositoryMock : WorkoutRepository {

    // Мок-данные тренировок
    private val mockWorkouts = listOf(
        WorkoutDto(
            id = 1,
            title = "Morning Yoga Flow",
            description = "Start your day with a refreshing yoga session. Perfect for beginners and experts alike.",
            type = WorkoutType.TRAINING,
            duration = "30"
        ),
        WorkoutDto(
            id = 2,
            title = "HIIT Cardio Blast",
            description = "High Intensity Interval Training to boost your metabolism and burn fat.",
            type = WorkoutType.TRAINING,
            duration = "45"
        ),
        WorkoutDto(
            id = 3,
            title = "Live Broadcast - Cardio Dance",
            description = "Join our live cardio dance session with professional instructors.",
            type = WorkoutType.BROADCAST,
            duration = "60 min"
        ),
        WorkoutDto(
            id = 4,
            title = "Strength Training Complex",
            description = "Full body workout complex focusing on strength and endurance.",
            type = WorkoutType.COMPLEX,
            duration = "50 min"
        ),
        WorkoutDto(
            id = 5,
            title = "Evening Meditation",
            description = "Relax and unwind with guided meditation. Reduce stress and improve sleep.",
            type = WorkoutType.TRAINING,
            duration = "20 min"
        ),
        WorkoutDto(
            id = 6,
            title = "Live Broadcast - Strength Training",
            description = "Live strength training session with real-time guidance.",
            type = WorkoutType.BROADCAST,
            duration = "40 min"
        ),
        WorkoutDto(
            id = 7,
            title = "Advanced Complex Routine",
            description = "Complex workout routine for advanced athletes. Push your limits.",
            type = WorkoutType.COMPLEX,
            duration = "55 min"
        ),
        WorkoutDto(
            id = 8,
            title = "Pilates Core Workout",
            description = "Core strengthening and flexibility training with Pilates.",
            type = WorkoutType.TRAINING,
            duration = "35 min"
        ),
        WorkoutDto(
            id = 9,
            title = "Live Zumba Party",
            description = "High-energy dance workout live session. Burn calories while having fun.",
            type = WorkoutType.BROADCAST,
            duration = "45 min"
        ),
        WorkoutDto(
            id = 10,
            title = "Full Body Complex",
            description = "Complete full body complex exercise program for all muscle groups.",
            type = WorkoutType.COMPLEX,
            duration = "40 min"
        ),
        WorkoutDto(
            id = 11,
            title = "Yoga for Flexibility",
            description = "Improve your flexibility and balance with these yoga poses.",
            type = WorkoutType.TRAINING,
            duration = "25 min"
        ),
        WorkoutDto(
            id = 12,
            title = "Live Broadcast - Yoga Flow",
            description = "Interactive live yoga flow session with instructor feedback.",
            type = WorkoutType.BROADCAST,
            duration = "50 min"
        )
    )

    // Мок-данные URL видео
    private val mockVideoUrls = mapOf(
        1 to "https://samplelib.com/mp4/sample-5s.mp4",
        2 to "https://samplelib.com/mp4/sample-5s.mp4",
        3 to "https://samplelib.com/mp4/sample-5s.mp4",
        4 to "https://samplelib.com/mp4/sample-5s.mp4",
        )

    private var shouldSimulateError = false
    private var shouldSimulateNetworkDelay = true
    private var customDelayMs = 800L
    private var errorRate = 0.1 // 10% вероятность ошибки
    private var callCount = 0

    override suspend fun getWorkouts(): Response<List<WorkoutDto>, BaseError> {
        callCount++

        if (shouldSimulateNetworkDelay) {
            delay(customDelayMs)
        }

        if (shouldSimulateError || Random.nextDouble() < errorRate) {
            return Response.Failure(
                BaseError.Api.Timeout
            )
        }

        return Response.Success(mockWorkouts)
    }

    override suspend fun getWorkoutVideo(id: Int): Response<WorkoutDetailsDto, BaseError> {
        if (shouldSimulateNetworkDelay) {
            delay(customDelayMs / 2)
        }

        val workout = mockWorkouts.find { it.id == id }
        if (workout == null) {
            return Response.Failure(
                BaseError.Api.Timeout
            )
        }

        val videoUrl = "https://devstreaming-cdn.apple.com/videos/streaming/examples/adv_dv_atmos/main.m3u8" //mockVideoUrls[id]
        if (videoUrl == null) {
            return Response.Failure(
                BaseError.Api.Timeout
            )
        }

        if (Random.nextDouble() < 0.05) {
            return Response.Failure(
                BaseError.Api.RequestFail("alallala")
            )
        }

        return Response.Success(
            WorkoutDetailsDto(
                id = id,
                duration = workout.duration,
                link = videoUrl
            )
        )
    }
}
