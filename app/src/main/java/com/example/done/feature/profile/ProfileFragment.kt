package com.example.done.feature.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.done.R
import com.example.done.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.profileSupport.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", "mahdiahmadi008@outlook.com", null)
            )
            startActivity(Intent.createChooser(intent, "ارسال ایمیل با"))
        }

        binding.profileInvite.setOnClickListener {
            val intent2 = Intent(Intent.ACTION_SEND);
            intent2.type = "text/plain"
            intent2.putExtra(Intent.EXTRA_TEXT,"دوستانت دعوت کن:\n link")
            startActivity(Intent.createChooser(intent2,  resources.getString(R.string.invite_friend)))
        }

        binding.profileStar.setOnClickListener {

        }

        return root
    }
}