package de.bitb.pantryplaner.core

import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.ui.base.testTags.AddEditChecklistDialogTag
import de.bitb.pantryplaner.ui.base.testTags.AddEditItemDialogTag
import de.bitb.pantryplaner.ui.base.testTags.AddEditStockDialogTag
import de.bitb.pantryplaner.ui.base.testTags.ChecklistPageTag
import de.bitb.pantryplaner.ui.base.testTags.StockPageTag
import de.bitb.pantryplaner.ui.base.testTags.TestTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

inline fun <reified T> parsePOKO(fileName: String): T {
    val json = readJsonFromAssets(fileName)
    return Gson().fromJson(json, T::class.java)
}

fun readJsonFromAssets(fileName: String): String {
    return InstrumentationRegistry.getInstrumentation()
        .context
        .assets
        .open("jsons/$fileName.json")
        .bufferedReader()
        .use { it.readText() }
}

fun getString(@StringRes id: Int, vararg args: Any): String {
    return InstrumentationRegistry.getInstrumentation()
        .targetContext
        .resources
        .getString(id, *args)
}

fun <T> createFlows(
    entries: List<T>,
    extractUUIDs: (T) -> List<String>
): MutableMap<String, MutableStateFlow<Result<List<T>>>> {
    return entries
        .flatMap { item -> extractUUIDs(item).map { uuid -> uuid to item } }
        .groupBy { it.first }
        .mapValues { (_, itemList) -> MutableStateFlow<Result<List<T>>>(Result.Success(itemList.map { it.second })) }
        .toMutableMap()
}

fun getParentTag(parent: String): TestTag {
    val tag = when {
        parent == "StockDialog" -> AddEditStockDialogTag.DialogTag
        parent == "ItemDialog" -> AddEditItemDialogTag.DialogTag
        parent == "ChecklistDialog" -> AddEditChecklistDialogTag.DialogTag
        parent.startsWith("StockPage") -> StockPageTag.StockPage(parent.replace("StockPage ", ""))
        parent == "ChecklistPage" -> ChecklistPageTag.ChecklistPage
        else -> throw AssertionError("Tag for \"$parent\" not found")
    }
    return tag
}

fun sleepFor(seconds: Int = 4) = runBlocking { delay(seconds * 1000L) }