package com.example.ecommerce.presentation.ui

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.rememberAsyncImagePainter
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecommerce.R
import com.example.ecommerce.data.model.DataX
import com.example.ecommerce.presentation.viewModel.GetCartsViewModel
import com.example.ecommerce.util.PreferencesManager

class FavoritesProductsDetails(
    private val product: DataX // Directly using product instead of products
) : Screen {
    @Composable
    override fun Content() {
        FavoritesProductDetailCard(product)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoritesProductDetailCard(product: DataX) {
    val navigator = LocalNavigator.currentOrThrow
    val context = LocalContext.current

    // State to manage toast messages
    var toastMessage by remember { mutableStateOf("") }
    val cartViewModel: GetCartsViewModel = hiltViewModel()
    val isInCartState = PreferencesManager.isProductInCart(context,product.product.id.toString())
    var isInCart by remember { mutableStateOf(isInCartState) }
    // State to manage if description is expanded or not
    var isExpanded by remember { mutableStateOf(false) }
    // Handle showing the toast messages
    LaunchedEffect(toastMessage) {
        if (toastMessage.isNotEmpty()) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            toastMessage = "" // Clear message after showing
        }
    }
    Log.d("","fuccckkkkkkkkk ${isInCart}")

    Scaffold(
        bottomBar = {
            // Add or Remove from Cart Button (fixed at the bottom)
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.White
            ) {
                Button(
                    onClick = {
                        isInCart = !isInCart // Toggle cart status
                        PreferencesManager.setCartStatus(context, product.product.id.toString(), isInCart)

                        if (isInCart) {
                            cartViewModel.addCartsOrDeleteCarts(product.product.id, onSuccess = {
                                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                            }, onError = {
                                Toast.makeText(context, "Error adding to cart", Toast.LENGTH_SHORT)
                                    .show()
                            })
                        } else {
                            cartViewModel.addCartsOrDeleteCarts(product.product.id, onSuccess = {
                                Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show()
                            }, onError = {
                                Toast.makeText(context, "Error removing from cart", Toast.LENGTH_SHORT)
                                    .show()
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White) // Padding for better UI appearance
                    ,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))

                ) {
                    Text(text = if (isInCart) "Remove from Cart" else "Add to Cart", color = Color.Black)
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Back Arrow
            IconButton(
                onClick = { navigator.pop() },
                modifier = Modifier
                    .padding(start = 16.dp, top = 25.dp)
                    .size(50.dp)

            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back_to_home)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Product Image
            Image(
                painter = rememberAsyncImagePainter(product.product.image),
                contentDescription = product.product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            // Product Information and Favorite Icon
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Product Name and Favorite Icon in a Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.product.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                }

                Spacer(modifier = Modifier.height(8.dp))

                // Product Description with "Read more"
                val formattedDescription = product.product.description.replace(Regex("\\. (?=[A-Za-z])"), ".\n")

                Text(
                    text = formattedDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                if (product.product.description.length > 100) {
                    Text(
                        text = if (isExpanded) stringResource(id = R.string.read_less) else stringResource(id = R.string.read_more),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable { isExpanded = !isExpanded }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${stringResource(id = R.string.price_label)}: ${product.product.price}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}
