package com.example.sub

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.example.sub.ui.login.LoginViewModel
import com.example.sub.data.model.LoggedInUser
import com.example.sub.ui.login.LoginViewModelFactory


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private var navController: NavController? = null
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(context))[LoginViewModel::class.java]
        navController = findNavController(view)
        view.findViewById<View>(R.id.AnnaKnappen).setOnClickListener {
            navController!!.navigate(
                R.id.action_profileFragment_to_userProfile
            )
        }
        view.findViewById<View>(R.id.logout).setOnClickListener {
            loginViewModel.loginRepository.logout()
            navController!!.navigate(
                R.id.action_profileFragment_to_loginFragment
            )
        }
    }

    private fun getLoggedInUser(): LoggedInUser? {
        return loginViewModel.loginRepository.user
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {}
            }
            requireActivity().onBackPressedDispatcher.addCallback(
                this,
                callback
            )
    }


    companion object {
        fun newInstance(): CallingFragment {
            return CallingFragment()
        }
    }
}