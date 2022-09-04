package com.example.festunavigator.presentation.orientating

import android.animation.ObjectAnimator
import android.graphics.Path
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.festunavigator.R
import com.example.festunavigator.databinding.FragmentOrientationBinding
import com.example.festunavigator.presentation.preview.MainShareModel
import com.example.festunavigator.presentation.scanner.ScannerFragment
import com.google.ar.core.Plane
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrientationFragment : Fragment() {

    private var _binding: FragmentOrientationBinding? = null
    private val binding get() = _binding!!

    private val mainModel: MainShareModel by activityViewModels()

    private var navigating = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrientationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainModel.frame.collect { frame ->
                    frame?.let {
                        if (frame.session.allPlanes.any { it.type == Plane.Type.VERTICAL }) {
                            if (!navigating){
                                navigating = true
                                val bundle = Bundle()
                                bundle.putInt(
                                    ScannerFragment.SCAN_TYPE,
                                    ScannerFragment.TYPE_INITIALIZE
                                )
                                findNavController().navigate(R.id.action_orientationFragment_to_scannerFragment, args = bundle)
                            }
                        }
                    }

                }
            }
        }
    }

    override fun onResume() {
        binding.imageView.on
        val x = binding.imageView.x
        val y = binding.imageView.y
        val animationPath = Path().apply {
            lineTo(x + x/2, y)
            lineTo(x, y - y/4)
            lineTo(x - x/2, y)
            lineTo(x, y)
        }
        val animationDuration = 3000L

        ObjectAnimator.ofFloat(binding.imageView, View.X, View.Y, animationPath).apply {
            duration = animationDuration
            repeatCount = Animation.INFINITE
            start()
        }
        super.onResume()
    }
}