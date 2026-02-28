package dev.danielk.workit.ui.grass

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dev.danielk.workit.R
import dev.danielk.workit.databinding.FragmentGrassBinding
import java.io.File
import java.io.FileOutputStream

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

        setupToolbar()

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

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.menu_grass)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_share -> {
                    captureAndShare()
                    true
                }
                else -> false
            }
        }
    }

    private fun captureAndShare() {
        val scrollView = binding.root.findViewById<View>(R.id.scroll_view) ?: return
        
        // Create bitmap of the entire ScrollView content
        val bitmap = Bitmap.createBitmap(
            scrollView.width,
            (scrollView as ViewGroup).getChildAt(0).height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // Background color
        scrollView.draw(canvas)

        try {
            val cachePath = File(requireContext().cacheDir, "shared_images")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/grass_capture.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val imagePath = File(requireContext().cacheDir, "shared_images")
            val newFile = File(imagePath, "grass_capture.png")
            val contentUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "dev.danielk.workit.fileprovider",
                newFile
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(contentUri, requireContext().contentResolver.getType(contentUri))
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
            }
            startActivity(Intent.createChooser(shareIntent, "잔디밭 공유하기"))

        } catch (e: Exception) {
            e.printStackTrace()
            android.widget.Toast.makeText(requireContext(), "공유 실패: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
