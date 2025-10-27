package com.example.healthflow.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.healthflow.R
import com.example.healthflow.data.PreferencesManager
import com.example.healthflow.databinding.FragmentSettingsBinding
import com.example.healthflow.notifications.HydrationReminderManager

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefsManager: PreferencesManager
    private lateinit var reminderManager: HydrationReminderManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager.getInstance(requireContext())
        reminderManager = HydrationReminderManager(requireContext())

        loadSettings()
        setupReminderSwitch()
        setupIntervalSelector()
        setupDarkModeSwitch()
    }

    private fun loadSettings() {
        // Load reminder enabled state
        val remindersEnabled = prefsManager.areRemindersEnabled()
        binding.switchReminders.isChecked = remindersEnabled
        binding.layoutInterval.visibility = if (remindersEnabled) View.VISIBLE else View.GONE

        // Load reminder interval
        val interval = prefsManager.getReminderInterval()
        when (interval) {
            1 -> binding.rb1min.isChecked = true
            30 -> binding.rb30min.isChecked = true
            60 -> binding.rb1hour.isChecked = true
            120 -> binding.rb2hours.isChecked = true
            180 -> binding.rb3hours.isChecked = true
        }

        // Load dark mode state
        binding.switchDarkMode.isChecked = prefsManager.isDarkModeEnabled()
    }

    private fun setupReminderSwitch() {
        binding.switchReminders.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.setRemindersEnabled(isChecked)

            if (isChecked) {
                binding.layoutInterval.visibility = View.VISIBLE
                val interval = prefsManager.getReminderInterval()
                reminderManager.scheduleReminders(interval)
                Toast.makeText(
                    requireContext(),
                    "Hydration reminders enabled",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.layoutInterval.visibility = View.GONE
                reminderManager.cancelReminders()
                Toast.makeText(
                    requireContext(),
                    "Hydration reminders disabled",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupIntervalSelector() {
        binding.rgInterval.setOnCheckedChangeListener { _, checkedId ->
            val interval = when (checkedId) {
                R.id.rb1min -> 1
                R.id.rb30min -> 30
                R.id.rb2hours -> 120
                R.id.rb3hours -> 180
                else -> 60 // Default 1 hour
            }

            prefsManager.setReminderInterval(interval)

            // Reschedule reminders with new interval
            if (prefsManager.areRemindersEnabled()) {
                reminderManager.scheduleReminders(interval)
                Toast.makeText(
                    requireContext(),
                    R.string.settings_saved,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupDarkModeSwitch() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.setDarkMode(isChecked)

            // Apply theme change
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            Toast.makeText(
                requireContext(),
                if (isChecked) "Dark mode enabled" else "Light mode enabled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}