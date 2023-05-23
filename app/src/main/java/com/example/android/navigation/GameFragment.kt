package com.example.android.navigation

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.navigation.database.User
import com.example.android.navigation.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    private lateinit var add: Button
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val user = arguments?.getParcelable<User>("user")
        val binding = FragmentGameBinding.inflate(inflater, container, false)
        if(user?.destinations?.size!! > 0) {
            val destinationsSize = user?.destinations?.get(user?.destinations?.size?.minus(1) ?: -1)?.imageResourceId

        }
        user?.let { // Check if user is not null
            add = binding.addButton
            add.setOnClickListener {
                view?.findNavController()?.navigate(GameFragmentDirections.actionGameFragmentToNewMemory(user))
            }
            binding.fullName.text = user.fullName
            binding.Email.text = user.email
        }

        // Create and set the adapter for the RecyclerView
        val destinationAdapter = DestinationAdapter(user?.destinations ?: emptyList(), user)
        binding.recyclerView.adapter = destinationAdapter

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = requireActivity() as AppCompatActivity
        drawerLayout = activity.findViewById(R.id.drawerLayout)
        val toggle = ActionBarDrawerToggle(
            activity, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
