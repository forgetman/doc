package test.compose.sampledata

import test.compose.model.Message

object SampleData {
    val conversationSample = listOf(
        "Alice",
        "Bob",
        "Carol",
        "Carol2",
        "Carol3",
        "Carol4",
        "Carol5",
        "Carol6",
        "Carol7",
        "Carol8",
        "Carol9",
        "Carol10"
    ).map {
        Message(it, "Hello, I'm $it")
    }
}