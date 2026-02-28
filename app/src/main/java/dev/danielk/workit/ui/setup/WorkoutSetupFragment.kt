package dev.danielk.workit.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import dev.danielk.workit.databinding.FragmentWorkoutSetupBinding
import dev.danielk.workit.model.TtsStyle
import dev.danielk.workit.model.WorkoutPreset

class WorkoutSetupFragment : Fragment() {

    private var _binding: FragmentWorkoutSetupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutSetupViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkoutSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPresetChips()
        setupTtsChips()
        bindInputs()
        observeSessionCreation()

        binding.btnStart.setOnClickListener {
            syncInputsToViewModel()
            viewModel.createAndStartSession()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupPresetChips() {
        WorkoutPreset.ALL.forEach { preset ->
            val chip = Chip(requireContext()).apply {
                text = "${preset.emoji} ${preset.name}"
                isCheckable = true
                setOnClickListener {
                    viewModel.applyPreset(preset)
                    updateInputFields()
                }
            }
            binding.chipGroupPresets.addView(chip)
        }
    }

    private fun setupTtsChips() {
        val styles = listOf(
            TtsStyle.COACH to "🔥 코치형",
            TtsStyle.FRIEND to "😊 친구형",
            TtsStyle.INFO to "📢 정보형"
        )
        styles.forEach { (style, label) ->
            val chip = Chip(requireContext()).apply {
                text = label
                isCheckable = true
                isChecked = style == TtsStyle.FRIEND
                setOnClickListener { viewModel.ttsStyle.value = style }
            }
            binding.chipGroupTts.addView(chip)
        }
    }

    private fun bindInputs() {
        binding.etWorkoutName.setText(viewModel.workoutName.value)
        binding.etReadySeconds.setText(viewModel.readySeconds.value.toString())
        binding.etWorkSeconds.setText(viewModel.workSeconds.value.toString())
        binding.etRestSeconds.setText(viewModel.restSeconds.value.toString())
        binding.etRepeatCount.setText(viewModel.repeatCount.value.toString())
    }

    private fun updateInputFields() {
        binding.etWorkoutName.setText(viewModel.workoutName.value)
        binding.etReadySeconds.setText(viewModel.readySeconds.value.toString())
        binding.etWorkSeconds.setText(viewModel.workSeconds.value.toString())
        binding.etRestSeconds.setText(viewModel.restSeconds.value.toString())
        binding.etRepeatCount.setText(viewModel.repeatCount.value.toString())
    }

    private fun syncInputsToViewModel() {
        viewModel.workoutName.value = binding.etWorkoutName.text.toString()
        viewModel.readySeconds.value = binding.etReadySeconds.text.toString().toIntOrNull() ?: 30
        viewModel.workSeconds.value = binding.etWorkSeconds.text.toString().toIntOrNull() ?: 60
        viewModel.restSeconds.value = binding.etRestSeconds.text.toString().toIntOrNull() ?: 60
        viewModel.repeatCount.value = binding.etRepeatCount.text.toString().toIntOrNull() ?: 6
    }

    private fun observeSessionCreation() {
        viewModel.createdSessionId.observe(viewLifecycleOwner) { sessionId ->
            if (sessionId != null) {
                val action = WorkoutSetupFragmentDirections.actionSetupToChat(
                    sessionId = sessionId,
                    autoStart = true
                )
                findNavController().navigate(action)
                viewModel.createdSessionId.value = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
