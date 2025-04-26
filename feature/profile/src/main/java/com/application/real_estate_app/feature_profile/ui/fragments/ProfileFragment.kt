package com.application.real_estate_app.feature_profile.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.application.real_estate_app.core_common.events.LogoutEvent
import com.application.real_estate_app.core_data.interfaces.IAuthRepository
import com.application.real_estate_app.feature_profile.R
import com.application.real_estate_app.feature_profile.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    @Inject
    lateinit var authApi: IAuthRepository

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
            authApi.signOut{ exception ->
                Toast.makeText(requireContext(), getString(R.string.log_out_failed, exception.message), Toast.LENGTH_SHORT).show()
            }
            EventBus.getDefault().post(LogoutEvent()) // Trigger the logout event
        }
        return  binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}