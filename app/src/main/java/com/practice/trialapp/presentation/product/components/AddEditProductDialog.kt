package com.practice.trialapp.presentation.product.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.practice.trialapp.R
import com.practice.trialapp.domain.model.Product

private const val MAX_NAME_LENGTH = 50
private val QUANTITY_OPTIONS = (10..100 step 10).toList()

@Composable
fun AddEditProductDialog(
    product: Product?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (name: String, cost: Double, quantity: Int) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(product?.name.orEmpty()) }
    var costText by rememberSaveable { mutableStateOf(product?.cost?.let { formatCostInput(it) }.orEmpty()) }
    var quantity by rememberSaveable { mutableIntStateOf(product?.quantity ?: QUANTITY_OPTIONS.first()) }

    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var costError by rememberSaveable { mutableStateOf<String?>(null) }

    val cost = costText.toDoubleOrNull()
    val totalPrice = (cost ?: 0.0) * quantity

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column {
                    Text(
                        text = if (product == null) "Add Product" else "Edit Product",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = if (product == null) "Fill in the product details" else "Update the product details",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FieldLabel("Product name") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            if (it.length <= MAX_NAME_LENGTH) {
                                name = it
                                nameError = null
                            }
                        },
                        placeholder = { Text("e.g. Wireless Mouse") },
                        singleLine = true,
                        isError = nameError != null,
                        supportingText = nameError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        shape = MaterialTheme.shapes.small,
                        colors = filledFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                FieldLabel("Product cost") {
                    OutlinedTextField(
                        value = costText,
                        onValueChange = { input ->
                            // allow only digits and a single dot, max 2 decimals
                            if (input.isEmpty() || input.matches(Regex("^\\d{0,9}(\\.\\d{0,2})?$"))) {
                                costText = input
                                costError = null
                            }
                        },
                        placeholder = { Text("0.00") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_currency_rupee),
                                contentDescription = "Rupees",
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        isError = costError != null,
                        supportingText = costError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = MaterialTheme.shapes.small,
                        colors = filledFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                FieldLabel("Quantity") {
                    QuantityDropdown(
                        selected = quantity,
                        onSelected = { quantity = it }
                    )
                }

                FieldLabel("Total price") {
                    OutlinedTextField(
                        value = formatPrice(totalPrice),
                        onValueChange = {},
                        readOnly = true,
                        textStyle = MaterialTheme.typography.titleMedium,
                        shape = MaterialTheme.shapes.small,
                        colors = filledFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        nameError = if (name.isBlank()) "Name is required" else null
                        costError = when {
                            costText.isBlank() -> "Cost is required"
                            cost == null -> "Enter a valid number"
                            cost <= 0.0 -> "Cost should be greater than 0"
                            else -> null
                        }
                        if (nameError == null && costError == null && cost != null) {
                            onSubmit(name, cost, quantity)
                        }
                    },
                    enabled = !isSaving,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = if (product == null) "Submit" else "Update",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        content()
    }
}

@Composable
private fun filledFieldColors(): TextFieldColors {
    val fill = MaterialTheme.colorScheme.surfaceVariant
    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = fill,
        unfocusedContainerColor = fill,
        errorContainerColor = fill,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Transparent,
        errorBorderColor = MaterialTheme.colorScheme.error,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.primary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuantityDropdown(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected.toString(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = MaterialTheme.shapes.small,
            colors = filledFieldColors(),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MaterialTheme.shapes.small,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            QUANTITY_OPTIONS.forEach { qty ->
                DropdownMenuItem(
                    text = { Text(qty.toString()) },
                    onClick = {
                        onSelected(qty)
                        expanded = false
                    }
                )
            }
        }
    }
}

// 120.0 -> "120", 99.5 -> "99.5"
private fun formatCostInput(cost: Double): String {
    return if (cost % 1.0 == 0.0) cost.toLong().toString() else cost.toString()
}
