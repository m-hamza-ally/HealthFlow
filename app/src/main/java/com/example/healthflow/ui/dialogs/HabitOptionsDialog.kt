package com.example.healthflow.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.healthflow.R
import com.example.healthflow.models.Habit

/**
 * Dialog showing options for a habit (Edit/Delete)
 */
class HabitOptionsDialog(
    private val habit: Habit,
    private val onEdit: () -> Unit,
    private val onDelete: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val options = arrayOf(
            getString(R.string.edit_habit),
            getString(R.string.delete_habit)
        )

        return AlertDialog.Builder(requireContext())
            .setTitle(habit.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> onEdit()
                    1 -> showDeleteConfirmation()
                }
            }
            .create()
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_habit_title)
            .setMessage(R.string.delete_habit_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                onDelete()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}