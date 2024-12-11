package com.application.real_estate_app.feature_profile.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.application.real_estate_app.core.events.LogoutEvent
import com.application.real_estate_app.domain.interfaces.AuthRepository
import com.application.real_estate_app.feature_profile.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    @Inject
    lateinit var auth: AuthRepository

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            EventBus.getDefault().post(LogoutEvent()) // Trigger the logout event
        }
        return  binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}