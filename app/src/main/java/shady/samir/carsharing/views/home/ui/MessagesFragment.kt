package shady.samir.carsharing.views.home.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import shady.samir.carsharing.adapters.UserMessageAdapter
import shady.samir.carsharing.data.datasources.SharedStorage
import shady.samir.carsharing.data.models.Message
import shady.samir.carsharing.data.models.MessageUser
import shady.samir.carsharing.data.models.User
import shady.samir.carsharing.databinding.FragmentMessagesBinding
import shady.samir.carsharing.utils.Methods
import shady.samir.carsharing.viewmodel.MessageViewModel
import shady.samir.carsharing.viewmodel.NetworkViewModel
import shady.samir.carsharing.viewmodel.UserViewModel

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null

    private val binding get() = _binding!!

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var userAdapter: UserMessageAdapter

    private lateinit var users: ArrayList<User>
    private lateinit var usersMsg: ArrayList<MessageUser>

    var myPhone: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        messageViewModel = ViewModelProvider(this)[MessageViewModel::class.java]

        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        users = ArrayList()
        usersMsg = ArrayList()

        myPhone = SharedStorage.getLoginPhoneData(requireContext()).toString()
        userAdapter = UserMessageAdapter(context, myPhone, viewLifecycleOwner, messageViewModel)

        binding.progress.visibility = VISIBLE

        binding.users.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        networkViewModel.networkState(context).observe(viewLifecycleOwner) {
            if (it) {
                getData()
            }
        }

        return root
    }

    private fun getData3() {
        binding.progress.visibility = VISIBLE

        userViewModel.getUsersWithListMsg(myPhone).observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                userAdapter.addDataMsg(usersMsg)
                binding.empty.visibility = GONE
            } else {
                binding.empty.visibility = VISIBLE
            }

            binding.progress.visibility = GONE

        }
    }

    private fun getData2() {
        userViewModel.getUsersData().observe(viewLifecycleOwner) {
            usersMsg.clear()
            if (it != null) {
                for (item in it.children) {
                    try {
                        val user = item.getValue(User::class.java)!!
                        if (user.userName != myPhone) {
                            val userMsg = MessageUser(user, null, null)
                            usersMsg.add(userMsg)
                        }
                    } catch (e: Exception) {
                        Log.e("Error", e.message.toString())
                    } finally {
                        binding.progress.visibility = GONE
                    }
                }
                Log.e("MSG", usersMsg.toString())

                if (usersMsg.isNotEmpty()) {
                    userAdapter.addDataMsg(usersMsg)
                    binding.empty.visibility = GONE
                } else {
                    binding.empty.visibility = VISIBLE
                }
            } else {
                binding.progress.visibility = GONE

            }
        }
    }


    private fun getData() {
        userViewModel.getUsersData().observe(viewLifecycleOwner) {
            if (it != null) {
                users.clear()
                usersMsg.clear()
                for (item in it.children) {
                    try {
                        val user = item.getValue(User::class.java)!!
                        if (user.userName != myPhone) {
                            //  usersMsg.add(MessageUser(user, "msg!!.dataMsg","Methods.getDateString(msg!!.date)"))
                            messageViewModel.getMessagesLastData(
                                Methods.getMessageId(
                                    myPhone,
                                    user.userPhone.toString()
                                )
                            ).observe(viewLifecycleOwner) {
                                if (it != null) {
                                    if (it.hasChildren()) {
                                        var msg: Message? = null
                                        it.children.forEach { item ->
                                            msg = item.getValue(Message::class.java)
                                        }
                                        Log.e("MSG", "true")
                                        if (msg != null) {
                                            Log.e("MSG", msg.toString())
                                            val userMsg =  MessageUser(
                                                user,
                                                msg!!.dataMsg.toString(),
                                                Methods.getDateString(
                                                    msg!!.date
                                                )
                                            )

                                            if (!usersMsg.contains(userMsg)){
                                                usersMsg.add(userMsg)
                                            }

                                            if (usersMsg.isNotEmpty()) {
                                                Log.e("MSGSize", usersMsg.size.toString())
                                                userAdapter.addDataMsg(usersMsg)
                                                binding.empty.visibility = GONE
                                            } else {
                                                binding.empty.visibility = VISIBLE
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    } catch (e: Exception) {
                        Log.e("Error", e.message.toString())
                    } finally {
                        binding.progress.visibility = GONE
                        binding.empty.visibility = VISIBLE
                    }
                }
                Log.e("MSG", usersMsg.toString())

            } else {
                binding.progress.visibility = GONE
                binding.empty.visibility = VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
