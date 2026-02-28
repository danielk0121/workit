package dev.danielk.workit.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import dev.danielk.workit.databinding.FragmentChatBinding
import dev.danielk.workit.model.WorkoutState

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private val args: ChatFragmentArgs by navArgs()
    private val adapter = MessageAdapter()

    private var isViewingOldSession = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerMessages.layoutManager = LinearLayoutManager(requireContext()).also {
            it.stackFromEnd = true
        }
        binding.recyclerMessages.adapter = adapter

        // autoStart=true → ViewModel loads session then starts timer
        viewModel.init(args.sessionId, args.autoStart)
        if (!args.autoStart) isViewingOldSession = true

        observeViewModel()
        setupInput()

        binding.toolbar.setNavigationOnClickListener {
            if (viewModel.isWorkoutActive.value == true) {
                AlertDialog.Builder(requireContext())
                    .setTitle("운동 중단")
                    .setMessage("운동을 중단하고 나가시겠어요?")
                    .setPositiveButton("중단") { _, _ ->
                        viewModel.stopWorkout()
                        findNavController().popBackStack()
                    }
                    .setNegativeButton("계속", null)
                    .show()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.sessionTitle.observe(viewLifecycleOwner) { title ->
            binding.toolbar.title = title
        }

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages.toList()) {
                if (messages.isNotEmpty()) {
                    binding.recyclerMessages.scrollToPosition(messages.size - 1)
                }
            }
        }

        viewModel.timerDisplay.observe(viewLifecycleOwner) { time ->
            binding.tvTimer.text = time
        }

        viewModel.quickReactions.observe(viewLifecycleOwner) { reactions ->
            updateQuickReactionChips(reactions)
        }

        viewModel.isInputEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.etMessage.isEnabled = enabled
            binding.btnSend.isEnabled = enabled
            binding.etMessage.hint = if (enabled) "메시지 입력..." else "휴식 시간에 입력할 수 있어요"
        }

        viewModel.currentState.observe(viewLifecycleOwner) { state ->
            binding.tvStateLabel.text = when (state) {
                WorkoutState.READY -> "⏱ 준비 중"
                WorkoutState.RUNNING -> "🏃 운동 중"
                WorkoutState.REST -> "😮‍💨 휴식 중"
                WorkoutState.DONE -> "🎉 완료"
            }
        }

        viewModel.workoutComplete.observe(viewLifecycleOwner) { complete ->
            if (complete) {
                binding.tvTimer.text = "완료!"
            }
        }
    }

    private fun updateQuickReactionChips(reactions: List<String>) {
        binding.chipGroupReactions.removeAllViews()
        reactions.forEach { reaction ->
            val chip = Chip(requireContext()).apply {
                text = reaction
                setOnClickListener { viewModel.sendQuickReaction(reaction) }
            }
            binding.chipGroupReactions.addView(chip)
        }
    }

    private fun setupInput() {
        binding.btnSend.setOnClickListener { sendMessage() }
        binding.etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else false
        }
        // Wrapper touch on input area when disabled → show warning via bot message
        binding.root.setOnClickListener { /* absorb */ }
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isNotEmpty()) {
            viewModel.sendTextMessage(text)
            binding.etMessage.text?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
