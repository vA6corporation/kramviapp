package com.example.kramviapp.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kramviapp.categories.CategoriesViewModel
import com.example.kramviapp.categories.CreateCategoriesDialog
import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.enums.PriceType
import com.example.kramviapp.login.LoginViewModel
import com.example.kramviapp.models.CreateProductModel
import com.example.kramviapp.models.NavigateTo
import com.example.kramviapp.models.PriceFieldModel
import com.example.kramviapp.models.PriceModel
import com.example.kramviapp.navigation.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductsScreen(
    innerUpc: String?,
    productsViewModel: ProductsViewModel,
    navigationViewModel: NavigationViewModel,
    categoriesViewModel: CategoriesViewModel,
    loginViewModel: LoginViewModel,
) {
    val categories by categoriesViewModel.categories.collectAsState()
    val setting by loginViewModel.setting.collectAsState()
    val offices by loginViewModel.offices.collectAsState()
    val unitCodes = productsViewModel.unitCodes

    var upc by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var isTrackStock by remember { mutableStateOf(false) }
    var stock by remember { mutableStateOf("") }
    var igvCode by remember { mutableStateOf(IgvCodeType.GRAVADO) }
    var unitCode by remember { mutableStateOf("NIU") }
    var unitCodeName by remember { mutableStateOf("UNIDADES (Productos)") }
    var priceFields: List<PriceFieldModel> by remember { mutableStateOf(listOf()) }

    var isShowCreateCategoriesDialog by remember { mutableStateOf(false) }

    var isValidName by remember { mutableStateOf(true) }
    var isValidPrice by remember { mutableStateOf(true) }
    var isValidCategory by remember { mutableStateOf(true) }
    var isEnabledSave by remember { mutableStateOf(true) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedIgvCode by remember { mutableStateOf(false) }
    var expandedUnitCode by remember { mutableStateOf(false) }

    if (isShowCreateCategoriesDialog) {
        CreateCategoriesDialog(categoriesViewModel, navigationViewModel) { category ->
            isShowCreateCategoriesDialog = false
            category?.let {
                categoryName = it.name
                categoryId = it._id
                categoriesViewModel.getCategories()
            }
        }
    }

    LaunchedEffect(offices) {
        when (setting.defaultPrice) {
            PriceType.GLOBAL -> {
                val priceField = PriceFieldModel(
                    "precio",
                    "",
                    null,
                    null,
                )
                priceFields = listOf(priceField)
            }

            PriceType.LISTA -> {

            }

            PriceType.OFICINA -> {
                offices?.let { offices ->
                    val mutablePriceFields: MutableList<PriceFieldModel> = mutableListOf()
                    for (office in offices) {
                        val priceField = PriceFieldModel(
                            "precio ${office.name}",
                            "",
                            null,
                            office._id,
                        )
                        mutablePriceFields.add(priceField)
                    }
                    priceFields = mutablePriceFields.toList()
                }
            }

            PriceType.LISTAOFICINA -> {

            }
        }
    }

    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Nuevo producto")
        if (categories == null) {
            categoriesViewModel.getCategories()
        }
        if (offices == null) {
            loginViewModel.loadOfficesByActivity()
        }
        innerUpc?.let { innerUpc ->
            if (innerUpc.isNotEmpty()) {
                upc = innerUpc
                navigationViewModel.loadBarStart()
                productsViewModel.getProductByUpcGlobal(
                    innerUpc,
                    onResponse = {
                        navigationViewModel.loadBarFinish()
                        name = it.fullName
                        for (priceField in priceFields) {
                            priceField.price = it.price.toString()
                        }
                    },
                    onFailure = {
                        navigationViewModel.loadBarFinish()
                    }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre/Modelo") },
            singleLine = true,
            maxLines = 1,
            isError = !isValidName,
        )
        Spacer(modifier = Modifier.height(12.dp))
        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = {
                expandedCategory = !expandedCategory
            }
        ) {
            TextField(
                readOnly = true,
                value = categoryName,
                onValueChange = { },
                label = { Text("Categoria") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandedCategory
                    )
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                isError = !isValidCategory
            )
            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false },
            ) {
                categories?.let { categories ->
                    for (category in categories) {
                        DropdownMenuItem(
                            onClick = {
                                categoryName = category.name
                                categoryId = category._id
                                expandedCategory = false
                            },
                            text = {
                                Text(category.name.uppercase())
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            enabled = isEnabledSave,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                isShowCreateCategoriesDialog = true
            },
        ) {
            Text(text = "NUEVA CATEGORIA")
        }
        Spacer(modifier = Modifier.height(12.dp))
        ExposedDropdownMenuBox(
            expanded = expandedIgvCode,
            onExpandedChange = {
                expandedIgvCode = !expandedIgvCode
            }
        ) {
            TextField(
                readOnly = true,
                value = igvCode.toString(),
                onValueChange = { },
                label = { Text("Declaracion") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandedIgvCode
                    )
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expandedIgvCode,
                onDismissRequest = { expandedIgvCode = false },
            ) {
                DropdownMenuItem(
                    onClick = {
                        igvCode = IgvCodeType.GRAVADO
                        expandedIgvCode = false
                    },
                    text = {
                        Text("GRAVADO")
                    }
                )
                DropdownMenuItem(
                    onClick = {
                        igvCode = IgvCodeType.EXONERADO
                        expandedIgvCode = false
                    },
                    text = {
                        Text("EXONERADO")
                    }
                )
                DropdownMenuItem(
                    onClick = {
                        igvCode = IgvCodeType.INAFECTO
                        expandedIgvCode = false
                    },
                    text = {
                        Text("INAFECTO")
                    }
                )
                DropdownMenuItem(
                    onClick = {
                        igvCode = IgvCodeType.BONIFICACION
                        expandedIgvCode = false
                    },
                    text = {
                        Text("BONIFICACION")
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        ExposedDropdownMenuBox(
            expanded = expandedUnitCode,
            onExpandedChange = {
                expandedUnitCode = !expandedUnitCode
            }
        ) {
            TextField(
                readOnly = true,
                value = unitCodeName,
                onValueChange = { },
                label = { Text("Unidad de medida") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandedIgvCode
                    )
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expandedUnitCode,
                onDismissRequest = { expandedUnitCode = false },
            ) {
                for (element in unitCodes) {
                    DropdownMenuItem(
                        onClick = {
                            unitCode = element.code
                            unitCodeName = element.name
                            expandedUnitCode = false
                        },
                        text = {
                            Text(element.name)
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = upc,
            onValueChange = { upc = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Codigo fabricante") },
            singleLine = true,
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = sku,
            onValueChange = { sku = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Codigo interno") },
            singleLine = true,
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(12.dp))
        for (priceField in priceFields) {
            var price by remember { mutableStateOf("") }
            TextField(
                value = price,
                onValueChange = {
                    price = it
                    priceField.price = price
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(priceField.name) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                maxLines = 1,
                isError = !isValidPrice,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        TextField(
            value = cost,
            onValueChange = { cost = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Costo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = isTrackStock,
                onCheckedChange = {
                    isTrackStock = it
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Trackear stock")
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (isTrackStock) {
            TextField(
                value = stock,
                onValueChange = { stock = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Stock inicial") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Button(
            enabled = isEnabledSave,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                for (priceField in priceFields) {
                    if (priceField.price.isEmpty()) {
                        isValidPrice = false
                        return@Button
                    }
                }

                if (name.isEmpty()) {
                    isValidName = false
                    return@Button
                }

                if (categoryId.isEmpty()) {
                    isValidCategory = false
                    return@Button
                }

                isEnabledSave = false
                navigationViewModel.loadBarStart()
                val product = CreateProductModel(
                    name,
                    sku,
                    upc,
                    categoryId,
                    priceFields[0].price.toDouble(),
                    unitCode,
                    igvCode,
                    isTrackStock,
                    if (stock.isEmpty()) 0.0 else stock.toDouble(),
                    listOf(),
                )
                val mutablePrices: MutableList<PriceModel> = mutableListOf()
                for (priceField in priceFields) {
                    val price = PriceModel(
                        priceField.price.toDouble(),
                        priceField.priceListId,
                        priceField.officeId
                    )
                    mutablePrices.add(price)
                }
                var prices: List<PriceModel> = listOf()
                when (setting.defaultPrice) {
                    PriceType.GLOBAL -> {

                    }
                    PriceType.LISTAOFICINA,
                    PriceType.OFICINA,
                    PriceType.LISTA -> {
                        prices = mutablePrices.toList()
                    }
                }
                productsViewModel.createProduct(
                    product,
                    prices,
                    onResponse = {
                        navigationViewModel.loadBarFinish()
                        navigationViewModel.onNavigateTo(NavigateTo("products"))
                        navigationViewModel.showMessage("Registrado correctamente")
                    },
                    onFailure = {
                        isEnabledSave = true
                        navigationViewModel.loadBarFinish()
                        navigationViewModel.showMessage(it)
                    }
                )
            },
        ) {
            Text(text = "GUARDAR")
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}