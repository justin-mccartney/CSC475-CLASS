package com.example.contactbook

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var contactListView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var contactList = mutableListOf<String>()
    private val PREFS_NAME = "contacts_prefs"
    private val CONTACTS_KEY = "contacts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etName = findViewById<EditText>(R.id.etName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        contactListView = findViewById(R.id.contactList)

        loadContacts()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactList)
        contactListView.adapter = adapter

        btnAdd.setOnClickListener {
            val name = etName.text.toString()
            val phone = etPhone.text.toString()
            if (name.isNotEmpty() && phone.isNotEmpty()) {
                val contact = "$name: $phone"
                contactList.add(contact)
                adapter.notifyDataSetChanged()
                saveContacts()
                etName.text.clear()
                etPhone.text.clear()
            } else {
                Toast.makeText(this, "Please enter both name and phone", Toast.LENGTH_SHORT).show()
            }
        }

        contactListView.setOnItemClickListener { _, _, position, _ ->
            showEditDeleteDialog(position)
        }
    }

    private fun showEditDeleteDialog(index: Int) {
        val selectedContact = contactList[index]
        val parts = selectedContact.split(": ")
        val currentName = parts[0]
        val currentPhone = parts[1]

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_contact, null)
        val etEditName = dialogView.findViewById<EditText>(R.id.etEditName)
        val etEditPhone = dialogView.findViewById<EditText>(R.id.etEditPhone)

        etEditName.setText(currentName)
        etEditPhone.setText(currentPhone)

        AlertDialog.Builder(this)
            .setTitle("Edit or Delete Contact")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = etEditName.text.toString()
                val newPhone = etEditPhone.text.toString()
                if (newName.isNotEmpty() && newPhone.isNotEmpty()) {
                    contactList[index] = "$newName: $newPhone"
                    adapter.notifyDataSetChanged()
                    saveContacts()
                }
            }
            .setNegativeButton("Delete") { _, _ ->
                contactList.removeAt(index)
                adapter.notifyDataSetChanged()
                saveContacts()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun saveContacts() {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putStringSet(CONTACTS_KEY, contactList.toSet())
            apply()
        }
    }

    private fun loadContacts() {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedContacts = sharedPref.getStringSet(CONTACTS_KEY, setOf())
        contactList = storedContacts?.toMutableList() ?: mutableListOf()
    }
}
