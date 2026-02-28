package dev.danielk.workit.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.danielk.workit.R
import dev.danielk.workit.databinding.FragmentProfileBinding
import dev.danielk.workit.model.TtsStyle

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private val badgeAdapter = BadgeAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.recyclerBadges.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerBadges.adapter = badgeAdapter

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.currentStreak.observe(viewLifecycleOwner) { streak ->
            binding.tvCurrentStreak.text = "🔥 ${streak}일"
        }

        viewModel.maxStreak.observe(viewLifecycleOwner) { maxStreak ->
            binding.tvMaxStreak.text = "🏆 ${maxStreak}일"
        }

        viewModel.badges.observe(viewLifecycleOwner) { badges ->
            badgeAdapter.submitList(badges)
        }

        viewModel.ttsStyle.observe(viewLifecycleOwner) { style ->
            when (style) {
                TtsStyle.COACH -> binding.rbCoach.isChecked = true
                TtsStyle.FRIEND -> binding.rbFriend.isChecked = true
                TtsStyle.INFO -> binding.rbInfo.isChecked = true
            }
        }

        viewModel.isDarkMode.observe(viewLifecycleOwner) { isEnabled ->
            if (binding.switchDarkMode.isChecked != isEnabled) {
                binding.switchDarkMode.isChecked = isEnabled
            }
        }

        viewModel.isReminderEnabled.observe(viewLifecycleOwner) { isEnabled ->
            if (binding.switchReminder.isChecked != isEnabled) {
                binding.switchReminder.isChecked = isEnabled
            }
        }

        viewModel.monthlyGoal.observe(viewLifecycleOwner) { goal ->
            binding.tvGoalValue.text = goal.toString()
            binding.progressMonthly.max = goal
            updateGoalProgress()
        }

        viewModel.monthlyWorkoutCount.observe(viewLifecycleOwner) { count ->
            updateGoalProgress()
        }
    }

    private fun updateGoalProgress() {
        val current = viewModel.monthlyWorkoutCount.value ?: 0
        val goal = viewModel.monthlyGoal.value ?: 10
        binding.tvMonthlyProgress.text = "${current} / ${goal}회 완료"
        binding.progressMonthly.progress = current
    }

    private fun setupListeners() {
        binding.rgTtsStyle.setOnCheckedChangeListener { _, checkedId ->
            val style = when (checkedId) {
                R.id.rb_coach -> TtsStyle.COACH
                R.id.rb_friend -> TtsStyle.FRIEND
                R.id.rb_info -> TtsStyle.INFO
                else -> TtsStyle.COACH
            }
            viewModel.setTtsStyle(style)
        }

        binding.btnIncreaseGoal.setOnClickListener {
            val current = viewModel.monthlyGoal.value ?: 10
            viewModel.setMonthlyGoal(current + 1)
        }

        binding.btnDecreaseGoal.setOnClickListener {
            val current = viewModel.monthlyGoal.value ?: 10
            if (current > 1) {
                viewModel.setMonthlyGoal(current - 1)
            }
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
        }

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setReminderEnabled(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
