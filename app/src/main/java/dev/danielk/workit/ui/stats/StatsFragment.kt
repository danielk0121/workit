package dev.danielk.workit.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dev.danielk.workit.databinding.FragmentStatsBinding
import dev.danielk.workit.model.MessageType
import dev.danielk.workit.model.WorkoutState
import dev.danielk.workit.ui.chat.ChatViewModel

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val args: StatsFragmentArgs by navArgs()
    private val viewModel: ChatViewModel by viewModels() // Reuse ChatViewModel for session data

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.init(args.sessionId)
        
        viewModel.sessionTitle.observe(viewLifecycleOwner) { title ->
            binding.tvTitle.text = title
        }

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            val emotionFlow = mutableMapOf<Int, MutableList<String>>()
            var currentRound = 1

            messages.forEach { msg ->
                // Bot messages update round
                if (msg.type == MessageType.BOT && msg.content.contains("달리세요!")) {
                    val match = Regex("""\((\d+)/(\d+)\)""").find(msg.content)
                    currentRound = match?.groupValues?.get(1)?.toIntOrNull() ?: currentRound
                }

                // User quick reactions are emotions
                if (msg.type == MessageType.USER_QUICK) {
                    val emotion = msg.content.take(2) // e.g. "😤"
                    emotionFlow.getOrPut(currentRound) { mutableListOf() }.add(emotion)
                }
            }

            val adapter = EmotionAdapter(emotionFlow.toSortedMap().toList())
            binding.recyclerEmotionFlow.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerEmotionFlow.adapter = adapter
            
            binding.tvInfo.text = "총 ${emotionFlow.size}개 라운드 기록됨"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
