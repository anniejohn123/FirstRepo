package com.example.tut1

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tut1.ui.theme.Tut1Theme
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.collections.emptyList
import com.google.gson.Gson
import java.io.InputStreamReader
import java.io.OutputStreamWriter


data class Student(
    val name: String,
    val rollNo: String
)

data class Company(
    val name: String,
    val deadline: String,
    val eligibleStudentsCount: List<Any>,
    val eligibleStudents: List<Student> = emptyList(),
    val ineligibleStudents: List<Student> = emptyList()
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tut1Theme {
                val companies by remember { mutableStateOf(readCompaniesFromJson()) }
                MainScreen(companies) { newCompany ->
                    saveCompaniesToJson(companies, applicationContext)
                }
            }
        }
    }

    private val gson: Gson = Gson()

    private fun readCompaniesFromJson(): List<Company> {
        val inputStream = application.assets.open("students.json")
        val reader = InputStreamReader(inputStream)
        return gson.fromJson(reader, Array<Company>::class.java).toList()
    }

    private fun saveCompaniesToJson(companies: List<Company>, context: Context) {
        val jsonString = gson.toJson(companies)
        val outputStream = context.openFileOutput("students.json", Context.MODE_PRIVATE)
        val writer = OutputStreamWriter(outputStream)
        writer.use {
            it.write(jsonString)
        }
    }


    @Composable
    fun MainScreen(
        companies: List<Company>,
        addCompany: (Company) -> Unit
    ) {
        var newCompanyName by remember { mutableStateOf("") }
        var newCompanyDeadline by remember { mutableStateOf("") }

        Column {
            // Display existing companies as clickable cards
            LazyColumn {
                items(companies) { company ->
                    CompanyCard(company)
                }
            }

            // Form to add a new company
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = newCompanyName,
                    onValueChange = { newCompanyName = it },
                    label = { Text("Company Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newCompanyDeadline,
                    onValueChange = { newCompanyDeadline = it },
                    label = { Text("Deadline") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    if (newCompanyName.isNotBlank() && newCompanyDeadline.isNotBlank()) {
                        addCompany(Company(newCompanyName, newCompanyDeadline, emptyList()))
                        newCompanyName = ""
                        newCompanyDeadline = ""
                    }
                }) {
                    Text("Add Company")
                }
            }
        }
    }


    @Composable
    fun CompanyCard(company: Company) {
        var expanded by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { expanded = !expanded },
            elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Company Name: ${company.name}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Deadline: ${company.deadline}")
                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Eligible Students:")
                    company.eligibleStudents.forEach { student ->
                        Text(text = "${student.name} - ${student.rollNo}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Ineligible Students:")
                    company.ineligibleStudents.forEach { student ->
                        Text(text = "${student.name} - ${student.rollNo}")
                    }
                }
            }
        }
    }
}

