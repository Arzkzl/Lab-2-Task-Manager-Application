package com.example.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.taskmanager.ui.theme.TaskManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskManagerTheme {
                MainScreen()
            }
        }
    }
}
//data model for one task
data class Task(
    val name: String,
    val completed: Boolean =false
)
@Composable
fun MainScreen(modifier:Modifier=Modifier) {
    //stores what user types in text field and remember keeps the value.
    val taskText = remember { mutableStateOf("") }

    //This list stores all tasks(list).
    val taskList = remember { mutableStateListOf<Task>() }
    //Places items vertically
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        //App title
        Text(
            text = "Task Manager",
            fontSize= 25.sp
        )
        //Adds empty space
        Spacer(modifier = Modifier.height(16.dp))

        //input section (TextField+Add button)
        TaskInputField(
            taskText =taskText.value,
            onTaskTextChange = {taskText.value= it},
            onAddTaskClick={
                //adds a new task and then clears the input field.
                taskList.add(Task(taskText.value))
                taskText.value=""
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        //shows all tasks
        TaskList(taskList=taskList)
    }
}

@Composable
fun TaskInputField(
    taskText:String,
    onTaskTextChange: (String) -> Unit,
    onAddTaskClick:() -> Unit
) {
    //row places the text field and button on the same line.
    Row(
        modifier=Modifier
            .fillMaxWidth(),
        verticalAlignment =Alignment.CenterVertically
    ){
        TextField(
            value =taskText,
            onValueChange={onTaskTextChange(it)},
            label= {Text ("Enter Task")},
            modifier = Modifier.weight(1f) // takes remaining space in the row
        )

        Spacer(modifier = Modifier.width(12.dp))

        Button(
            onClick = onAddTaskClick,
            colors= ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9D00FF)
            )
        ) {
            Text("Add Task")
        }
    }
}

@Composable
fun TaskList(taskList: MutableList<Task>) {
    //creates a scrollable list.
    LazyColumn {
        items(taskList) {task ->
            TaskItem(
                task = task,
                onCheckedChange = {isChecked ->
                    //I first tried task.completed=isChecked, but the checkbox was not triggering correctly.
                    //I researched the issue and found that replacing the task using copy() makes Compose refresh the UI.
                    val index = taskList.indexOf(task)
                    if(index!=-1)
                        taskList[index]=task.copy(completed = isChecked)
                },
                onDeleteClick = {
                    //removes the selected task from the list
                    taskList.remove(task)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

    }
}

@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick:() -> Unit
){
    Row(
        modifier=Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        //shows completed status
        Checkbox(
            checked=task.completed,
            onCheckedChange=onCheckedChange,
            colors= CheckboxDefaults.colors(
                checkedColor=Color(0xFF9D00FF)
            )
        )
        //task (line-through when completed)
        Text(
            text=task.name,
            modifier= Modifier.weight(1f),
            textDecoration = if (task.completed)
                TextDecoration.LineThrough
            else
                TextDecoration.None
        )
        //delete icon button
        IconButton(onClick=onDeleteClick) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TaskManagerTheme {
        MainScreen()
    }
}
