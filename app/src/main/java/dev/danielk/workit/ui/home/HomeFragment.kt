package dev.danielk.workit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.danielk.workit.R
import dev.danielk.workit.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private val adapter = SessionAdapter(
        onClick = { session ->
            val action = HomeFragmentDirections.actionHomeToChat(session.id)
            findNavController().navigate(action)
        },
        onLongClick = { session ->
            AlertDialog.Builder(requireContext())
                .setTitle("삭제")
                .setMessage("'${session.title}'을 삭제할까요?")
                .setPositiveButton("삭제") { _, _ -> viewModel.deleteSession(session) }
                .setNegativeButton("취소", null)
                .show()
        }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.inflateMenu(R.menu.menu_home)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    findNavController().navigate(R.id.action_home_to_profile)
                    true
                }
                R.id.menu_filter -> {
                    showFilterDialog()
                    true
                }
                R.id.menu_sort -> {
                    toggleSort()
                    true
                }
                else -> false
            }
        }

        binding.recyclerSessions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSessions.adapter = adapter

        viewModel.filteredSessions.observe(viewLifecycleOwner) { sessions ->
            adapter.submitList(sessions)
            binding.tvEmpty.visibility = if (sessions.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabNewWorkout.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_setup)
        }

        setupGrassPreview()
    }

    private fun toggleSort() {
        viewModel.sortDescending.value = !viewModel.sortDescending.value
        val label = if (viewModel.sortDescending.value) "최신순으로 정렬됩니다" else "오래된 순으로 정렬됩니다"
        android.widget.Toast.makeText(requireContext(), label, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showFilterDialog() {
        val months = listOf("전체") + viewModel.getAvailableMonths()
        val currentFilter = viewModel.filterMonth.value
        val currentIndex = if (currentFilter == null) 0 else months.indexOf(currentFilter).takeIf { it >= 0 } ?: 0

        AlertDialog.Builder(requireContext())
            .setTitle("월 선택")
            .setSingleChoiceItems(months.toTypedArray(), currentIndex) { dialog, which ->
                viewModel.filterMonth.value = if (which == 0) null else months[which]
                dialog.dismiss()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun setupGrassPreview() {
        binding.grassPreview.setWeeksCount(13) // ~3 months
        viewModel.grassRecords.observe(viewLifecycleOwner) { records ->
            binding.grassPreview.setRecords(records)
            val streak = viewModel.getCurrentStreak(records)
            binding.tvStreakBanner.text = if (streak > 0) "🔥 ${streak}일 연속 운동 중!" else "오늘 운동을 시작해볼까요?"
        }

        binding.grassPreview.onDateClick = { date ->
            val record = viewModel.grassRecords.value?.find { it.date == date }
            if (record != null && record.sessionId > 0) {
                val action = HomeFragmentDirections.actionHomeToChat(record.sessionId)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
