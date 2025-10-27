package com.example.healthflow.ui.steps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.healthflow.R
import com.example.healthflow.data.PreferencesManager
import com.example.healthflow.databinding.FragmentStepsBinding
import kotlin.math.roundToInt
import kotlin.math.sqrt

class StepsFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentStepsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null

    private lateinit var prefsManager: PreferencesManager

    private var isTracking = false
    private var totalSteps = 0
    private var sessionSteps = 0
    private var initialSteps = 0
    private var hasInitialStepsBeenSet = false

    // For accelerometer-based step detection
    private var lastAcceleration = 0.0
    private var currentAcceleration = 0.0
    private var lastStepTime = 0L
    private val stepThreshold = 2.0 // Lowered for better sensitivity
    private val stepDelayMs = 250L

    companion object {
        private const val STEP_GOAL = 10000
        private const val STEPS_TO_KM = 0.0008
        private const val CALORIES_PER_STEP = 0.04
    }

    // Permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Permission granted!", Toast.LENGTH_SHORT).show()
            initializeSensors()
        } else {
            Toast.makeText(
                requireContext(),
                "Permission denied. Step counter won't work.",
                Toast.LENGTH_LONG
            ).show()
            binding.btnToggleTracking.isEnabled = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager.getInstance(requireContext())
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        checkPermissionAndInitialize()
        loadTodaySteps()
        setupToggleButton()
    }

    private fun checkPermissionAndInitialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    initializeSensors()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION) -> {
                    // Show explanation and request permission
                    Toast.makeText(
                        requireContext(),
                        "Permission needed to count steps",
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            }
        } else {
            // No permission needed for Android 9 and below
            initializeSensors()
        }
    }

    private fun initializeSensors() {
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (stepCounterSensor != null) {
            binding.tvSensorStatus.text = "Sensor: Step Counter Available ✅"
            binding.tvSensorStatus.setTextColor(resources.getColor(R.color.success_green, null))
            Toast.makeText(requireContext(), "Using hardware step counter", Toast.LENGTH_SHORT).show()
        } else if (accelerometerSensor != null) {
            binding.tvSensorStatus.text = "Sensor: Using Accelerometer ⚡"
            binding.tvSensorStatus.setTextColor(resources.getColor(R.color.accent_orange, null))
            Toast.makeText(requireContext(), "Using accelerometer for step detection", Toast.LENGTH_SHORT).show()
        } else {
            binding.tvSensorStatus.text = "Sensor: Not Available ❌"
            binding.tvSensorStatus.setTextColor(resources.getColor(R.color.error_red, null))
            binding.btnToggleTracking.isEnabled = false
        }
    }

    private fun loadTodaySteps() {
        totalSteps = prefsManager.getTodaySteps()
        sessionSteps = 0
        updateUI()
    }

    private fun setupToggleButton() {
        binding.btnToggleTracking.setOnClickListener {
            if (isTracking) {
                stopTracking()
            } else {
                startTracking()
            }
        }
    }

    private fun startTracking() {
        isTracking = true
        sessionSteps = 0
        initialSteps = 0
        hasInitialStepsBeenSet = false

        // Register appropriate sensor
        if (stepCounterSensor != null) {
            val registered = sensorManager.registerListener(
                this,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            if (registered) {
                Toast.makeText(
                    requireContext(),
                    "Hardware step counter active. Start walking!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to register step counter",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (accelerometerSensor != null) {
            sensorManager.registerListener(
                this,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
            Toast.makeText(
                requireContext(),
                "Accelerometer active. Start walking!",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnToggleTracking.text = "Stop Tracking"
        binding.btnToggleTracking.setIconResource(R.drawable.ic_stop)
    }

    private fun stopTracking() {
        isTracking = false
        sensorManager.unregisterListener(this)

        binding.btnToggleTracking.text = "Start Tracking"
        binding.btnToggleTracking.setIconResource(R.drawable.ic_play)

        // Save steps
        prefsManager.saveTodaySteps(totalSteps)

        Toast.makeText(
            requireContext(),
            "Tracking stopped. Session steps: $sessionSteps",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isTracking || event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                handleStepCounter(event)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                handleAccelerometer(event)
            }
        }
    }

    private fun handleStepCounter(event: SensorEvent) {
        val currentStepCount = event.values[0].toInt()

        if (!hasInitialStepsBeenSet) {
            // Set initial steps on first reading
            initialSteps = currentStepCount
            hasInitialStepsBeenSet = true
            Toast.makeText(
                requireContext(),
                "Baseline set: $initialSteps. Start walking!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Calculate session steps
            sessionSteps = currentStepCount - initialSteps

            if (sessionSteps > 0) {
                totalSteps = prefsManager.getTodaySteps() + sessionSteps
                updateUI()

                // Feedback every 10 steps
                if (sessionSteps % 10 == 0) {
                    Toast.makeText(
                        requireContext(),
                        "Steps: $sessionSteps",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun handleAccelerometer(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Calculate acceleration magnitude
        lastAcceleration = currentAcceleration
        currentAcceleration = sqrt((x * x + y * y + z * z).toDouble())
        val delta = currentAcceleration - lastAcceleration
        val currentTime = System.currentTimeMillis()

        // Detect step when acceleration change exceeds threshold
        if (delta > stepThreshold && currentTime - lastStepTime > stepDelayMs) {
            sessionSteps++
            totalSteps++
            lastStepTime = currentTime
            updateUI()

            // Visual feedback every 5 steps
            if (sessionSteps % 5 == 0) {
                Toast.makeText(
                    requireContext(),
                    "Steps: $sessionSteps",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }

    private fun updateUI() {
        binding.tvStepCount.text = totalSteps.toString()
        binding.progressSteps.progress = totalSteps.coerceAtMost(STEP_GOAL)

        val distance = (totalSteps * STEPS_TO_KM * 10).roundToInt() / 10.0
        binding.tvDistance.text = "$distance km"

        val calories = (totalSteps * CALORIES_PER_STEP).roundToInt()
        binding.tvCalories.text = "$calories kcal"

        val activeMinutes = (totalSteps / 100).coerceAtMost(999)
        binding.tvActiveTime.text = "$activeMinutes min"

        val remaining = (STEP_GOAL - totalSteps).coerceAtLeast(0)
        binding.tvGoalText.text = if (totalSteps >= STEP_GOAL) {
            "Goal achieved! 🎉"
        } else {
            "Goal: $STEP_GOAL steps ($remaining remaining)"
        }

        // Save progress
        if (isTracking && sessionSteps > 0) {
            prefsManager.saveTodaySteps(totalSteps)
        }
    }

    override fun onPause() {
        super.onPause()
        // Keep tracking in background if needed
    }

    override fun onResume() {
        super.onResume()
        loadTodaySteps()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isTracking) {
            sensorManager.unregisterListener(this)
        }
        _binding = null
    }
}