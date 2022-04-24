package com.example.sub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController

/**
 * A simple [Fragment] subclass.
 * Use the [userProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class userProfile : Fragment() {
    var navController: NavController? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController(view.findViewById(R.id.callButton))
        view.findViewById<View>(R.id.callButton).setOnClickListener {
            navController!!.navigate(
                R.id.action_userProfile_to_callingFragment
            )
        }
        view.findViewById<View>(R.id.profile).setOnClickListener {
            navController!!.navigate(
                R.id.action_userProfile_to_profileFragment
            )
        }
    }

    companion object {
        fun newInstance(): callingFragment {
            return callingFragment()
        }
    }
}