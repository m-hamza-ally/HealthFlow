package com.example.healthflow.ui.mood

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthflow.R
import com.example.healthflow.data.PreferencesManager
import com.example.healthflow.databinding.FragmentMoodBinding
import com.example.healthflow.models.MoodEntry
import com.example.healthflow.models.MoodType
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class MoodFragment : Fragment() {

    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefsManager: PreferencesManager
    private lateinit var moodEmojiAdapter: MoodEmojiAdapter
    private lateinit var moodHistoryAdapter: MoodHistoryAdapter

    private var selectedMood: MoodType? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager.getInstance(requireContext())

        setupMoodSelector()
        setupMoodHistory()
        setupSaveButton()
        loadMoodHistory()
    }

    private fun setupMoodSelector() {
        val moods = MoodType.values().toList()

        moodEmojiAdapter = MoodEmojiAdapter(moods) { mood ->
            selectedMood = mood
        }

        binding.rvMoodEmojis.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = moodEmojiAdapter
        }
    }

    private fun setupMoodHistory() {
        moodHistoryAdapter = MoodHistoryAdapter { moodEntry ->
            deleteMoodEntry(moodEntry)
        }

        binding.rvMoodHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodHistoryAdapter
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveMood.setOnClickListener {
            saveMoodEntry()
        }
    }

    private fun saveMoodEntry() {
        if (selectedMood == null) {
            Toast.makeText(
                requireContext(),
                R.string.error_no_mood_selected,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val note = binding.etNote.text.toString().trim()

        val moodEntry = MoodEntry(
            mood = selectedMood!!,
            note = note
        )

        prefsManager.addMoodEntry(moodEntry)

        Toast.makeText(
            requireContext(),
            R.string.mood_logged,
            Toast.LENGTH_SHORT
        ).show()

        // Clear inputs
        binding.etNote.text?.clear()
        selectedMood = null
        moodEmojiAdapter.clearSelection()

        // Reload history and update chart
        loadMoodHistory()
    }

    private fun loadMoodHistory() {
        val moodEntries = prefsManager.getMoodEntries()

        if (moodEntries.isEmpty()) {
            binding.rvMoodHistory.visibility = View.GONE
            binding.cardChart.visibility = View.GONE
            binding.tvMoodTrends.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvMoodHistory.visibility = View.VISIBLE
            binding.cardChart.visibility = View.VISIBLE
            binding.tvMoodTrends.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
            moodHistoryAdapter.submitList(moodEntries)
            updateMoodChart()
        }
    }

    private fun updateMoodChart() {
        val moodEntries = prefsManager.getMoodEntries()
        if (moodEntries.isEmpty()) return

        // Check if dark mode is enabled
        val isDarkMode = (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        val textColor = if (isDarkMode) {
            Color.parseColor("#E1E1E1") // Light text for dark mode
        } else {
            Color.parseColor("#2D3142") // Dark text for light mode
        }

        val gridColor = if (isDarkMode) {
            Color.parseColor("#3D3D3D") // Darker grid for dark mode
        } else {
            Color.parseColor("#F5F5F5") // Light grid for light mode
        }

        // Get last 7 days of mood data
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayLabels = mutableListOf<String>()
        val moodValues = mutableListOf<Entry>()

        // Create map of dates to mood values
        val moodByDate = moodEntries.groupBy { it.getDateKey() }

        for (i in 6 downTo 0) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dateKey = dateFormat.format(calendar.time)
            val dayLabel = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            dayLabels.add(dayLabel)

            // Calculate average mood for the day (1-5 scale)
            val dayMoods = moodByDate[dateKey]
            val avgMood = if (dayMoods != null && dayMoods.isNotEmpty()) {
                dayMoods.map { getMoodValue(it.mood) }.average().toFloat()
            } else {
                0f // No mood logged
            }

            moodValues.add(Entry((6 - i).toFloat(), avgMood))
        }

        // Create dataset
        val dataSet = LineDataSet(moodValues, "Mood Level").apply {
            color = Color.parseColor("#6BB6FF")
            setCircleColor(Color.parseColor("#6BB6FF"))
            lineWidth = 3f
            circleRadius = 6f
            setDrawCircleHole(false)
            valueTextSize = 12f
            valueTextColor = textColor
            setDrawFilled(true)
            fillColor = Color.parseColor("#6BB6FF")
            fillAlpha = 50
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(true)
        }

        // Set data
        val lineData = LineData(dataSet)
        binding.moodChart.data = lineData

        // Configure chart
        binding.moodChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)

            // X-axis configuration
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                this.textColor = textColor
                textSize = 11f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index in dayLabels.indices) dayLabels[index] else ""
                    }
                }
            }

            // Left Y-axis
            axisLeft.apply {
                setDrawGridLines(true)
                this.gridColor = gridColor
                axisMinimum = 0f
                axisMaximum = 5f
                granularity = 1f
                this.textColor = textColor
                textSize = 11f
            }

            // Right Y-axis
            axisRight.isEnabled = false

            // Legend
            legend.apply {
                isEnabled = true
                this.textColor = textColor
                textSize = 12f
            }

            // Refresh chart
            invalidate()
        }
    }

    private fun getMoodValue(mood: MoodType): Int {
        return when (mood) {
            MoodType.ANGRY -> 1
            MoodType.SAD -> 2
            MoodType.STRESSED -> 2
            MoodType.OKAY -> 3
            MoodType.HAPPY -> 4
            MoodType.AMAZING -> 5
        }
    }

    private fun deleteMoodEntry(moodEntry: MoodEntry) {
        prefsManager.deleteMoodEntry(moodEntry.id)
        loadMoodHistory()
        Toast.makeText(
            requireContext(),
            "Mood entry deleted",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}