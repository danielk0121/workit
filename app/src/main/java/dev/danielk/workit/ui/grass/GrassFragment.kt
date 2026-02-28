package dev.danielk.workit.ui.grass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dev.danielk.workit.databinding.FragmentGrassBinding

class GrassFragment : Fragment() {

    private var _binding: FragmentGrassBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GrassViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGrassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.grassRecords.observe(viewLifecycleOwner) { records ->
            binding.grassView.setRecords(records)
            val streak = viewModel.getCurrentStreak(records)
            viewModel.currentStreak.value = streak
            binding.tvStreakBanner.text = if (streak > 0) "🔥 ${streak}일 연속 운동 중!" else "오늘 운동을 시작해볼까요?"
            binding.tvCurrentStreak.text = "${streak}일"
        }

        viewModel.totalWorkoutDays.observe(viewLifecycleOwner) { days ->
            binding.tvTotalDays.text = "${days}일"
        }

        viewModel.maxStreak.observe(viewLifecycleOwner) { max ->
            binding.tvMaxStreak.text = "${max}일"
        }

        binding.grassView.onDateClick = { date ->
            val record = viewModel.grassRecords.value?.find { it.date == date }
            if (record != null && record.sessionId > 0) {
                val action = GrassFragmentDirections.actionGrassToChat(record.sessionId)
                findNavController().navigate(action)
            } else {
                android.widget.Toast.makeText(requireContext(), "$date: 운동 기록이 없습니다.", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
