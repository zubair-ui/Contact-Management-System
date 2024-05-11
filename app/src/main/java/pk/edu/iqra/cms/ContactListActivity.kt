package pk.edu.iqra.cms

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pk.edu.iqra.cms.adapter.ContactAdapter
import pk.edu.iqra.cms.database.AppDatabase
import pk.edu.iqra.cms.database.Contact
import pk.edu.iqra.cms.databinding.ActivityContactListBinding
import pk.edu.iqra.cms.listener.ListAction
import pk.edu.iqra.cms.utils.DataHolder

class ContactListActivity : AppCompatActivity(), ListAction {
    private lateinit var binding:ActivityContactListBinding
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initializeRV()

        binding.btnManageContact.setOnClickListener {
            addContact()
        }
    }

    fun initializeRV(){
        adapter = ContactAdapter(arrayListOf(), this)
        binding.rvContactList.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    fun refreshList(){
        GlobalScope.launch(Dispatchers.IO) {
            val contacts = AppDatabase.getDatabase(this@ContactListActivity).contactDao().getAllContacts()
            launch(Dispatchers.Main) {
                adapter.updateList(contacts)
            }
        }
    }

    override fun onClick(contact: Contact) {
        AlertDialog
            .Builder(this)
            .setTitle("Action required")
            .setMessage("Please choose relevant action for contact named ${contact.name}")
            .setPositiveButton("Update") { dialog,i ->
                updateContact(contact)
            }
            .setNegativeButton("Delete") { dialog,i ->
                deleteContact(contact)
            }
            .setNeutralButton("Cancel") { dialog,i ->
                dialog::dismiss
            }
            .create()
            .show()
    }

    fun addContact(){
        DataHolder.contact = null
        navigate()
    }

    fun updateContact(contact: Contact){
        DataHolder.contact = contact
        navigate()
    }

    fun navigate(){
        Intent(this,ContactManageActivity::class.java).apply {
            startActivity(this)
        }
    }

    fun deleteContact(contact: Contact){
        GlobalScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(this@ContactListActivity).contactDao().delete(contact)
            refreshList()
        }
    }
}