package com.example.healthflow.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.healthflow.R
import com.example.healthflow.databinding.DialogAddEditHabitBinding
import com.example.healthflow.models.Habit

/**
 * Dialog for adding or editing a habit
 */
class AddEditHabitDialog(
    private val habit: Habit? = null,
    private val onSave: (Habit) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddEditHabitBinding? = null
    private val binding get() = _binding!!

    private var selectedIcon: String = "💧"
    private var selectedColor: String = "#6BB6FF"

    private lateinit var iconAdapter: IconAdapter
    private lateinit var colorAdapter: ColorAdapter

    // Available icons
    private val icons = listOf(
        "💧", "🧘", "🚶", "📚", "🏃", "🍎",
        "😴", "💪", "🎯", "✍️", "🧠", "❤️",
        "☕", "🥗", "🎵", "🌞", "🌙", "⭐"
    )

    // Available colors
    private val colors = listOf(
        "#6BB6FF", "#7ED957", "#B19EFF", "#FFB054",
        "#FF9ECD", "#FF6B6B", "#7FFFD4", "#FFD93D"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddEditHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupIconSelector()
        setupColorSelector()
        setupButtons()

        // If editing, populate fields
        habit?.let { populateFields(it) }
    }

    private fun setupUI() {
        // Set title based on mode
        binding.tvDialogTitle.text = if (habit == null) {
            getString(R.string.add_habit)
        } else {
            getString(R.string.edit_habit)
        }
    }

    private fun setupIconSelector() {
        iconAdapter = IconAdapter(icons) { icon ->
            selectedIcon = icon
        }

        binding.rvIcons.apply {
            layoutManager = GridLayoutManager(requireContext(), 6)
            adapter = iconAdapter
        }

        // Select first icon by default
        iconAdapter.selectIcon(selectedIcon)
    }

    private fun setupColorSelector() {
        colorAdapter = ColorAdapter(colors) { color ->
            selectedColor = color
        }

        binding.rvColors.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = colorAdapter
        }

        // Select first color by default
        colorAdapter.selectColor(selectedColor)
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveHabit()
        }
    }

    private fun populateFields(habit: Habit) {
        binding.etHabitName.setText(habit.name)
        selectedIcon = habit.icon
        selectedColor = habit.color

        iconAdapter.selectIcon(selectedIcon)
        colorAdapter.selectColor(selectedColor)
    }

    private fun saveHabit() {
        val name = binding.etHabitName.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(
                requireContext(),
                R.string.error_empty_habit_name,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val newHabit = if (habit == null) {
            // Creating new habit
            Habit(
                name = name,
                icon = selectedIcon,
                color = selectedColor
            )
        } else {
            // Updating existing habit
            habit.copy(
                name = name,
                icon = selectedIcon,
                color = selectedColor
            )
        }

        onSave(newHabit)

        Toast.makeText(
            requireContext(),
            if (habit == null) R.string.habit_added else R.string.habit_updated,
            Toast.LENGTH_SHORT
        ).show()

        dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}