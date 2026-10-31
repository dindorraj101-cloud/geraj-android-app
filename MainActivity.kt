package com.geraj.mechanic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.geraj.mechanic.data.FirebaseRepository
import com.geraj.mechanic.model.*
import com.geraj.mechanic.ui.navigation.Screen
import com.geraj.mechanic.ui.screens.*
import com.geraj.mechanic.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val repository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GERAJTheme {
                MainAppScreen(repository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(repository: FirebaseRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var selectedVehicle by remember { mutableStateOf(VehicleType.CAR) }
    var selectedProblem by remember { mutableStateOf("Flat Tyre / Puncture") }
    var userRole by remember { mutableStateOf(UserRole.CUSTOMER) }

    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var mechanicProfile by remember { mutableStateOf<Mechanic?>(null) }
    var authModalOpen by remember { mutableStateOf(false) }

    var mechanics by remember { mutableStateOf<List<Mechanic>>(MockData.initialMechanics) }
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var selectedMechanicForBooking by remember { mutableStateOf<Mechanic?>(null) }

    // Listen to mechanics flow
    LaunchedEffect(Unit) {
        repository.observeMechanics().collect {
            mechanics = it
        }
    }

    // Listen to bookings flow
    LaunchedEffect(userProfile, userRole) {
        val uid = userProfile?.uid ?: "guest"
        repository.observeBookings(uid, userRole == UserRole.MECHANIC).collect {
            bookings = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = DarkBackground,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "GERAJ",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "RESCUE",
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = if (userRole == UserRole.MECHANIC) "Garage Terminal · ₹ INR" else "Breakdown Dispatch · ₹ INR",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    // Emergency 112 Call Button
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("112 SOS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardBackground,
                contentColor = TextPrimary,
                tonalElevation = 8.dp
            ) {
                val screens = listOf(
                    Screen.Home,
                    Screen.Find,
                    Screen.Bookings,
                    Screen.Profile
                )
                screens.forEach { screen ->
                    val isSelected = currentScreen.route == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (isSelected) NeonGreen else TextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                color = if (isSelected) NeonGreen else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = NeonGreen.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.Home -> HomeScreen(
                    selectedVehicle = selectedVehicle,
                    onSelectVehicle = { selectedVehicle = it },
                    selectedProblem = selectedProblem,
                    onSelectProblem = { selectedProblem = it },
                    mechanics = mechanics,
                    onFindMechanicsClick = { currentScreen = Screen.Find },
                    onSelectMechanicForBooking = { mech ->
                        selectedMechanicForBooking = mech
                    }
                )
                Screen.Find -> FindMechanicsScreen(
                    mechanics = mechanics,
                    selectedVehicle = selectedVehicle,
                    onSelectVehicle = { selectedVehicle = it },
                    onBookMechanic = { mech ->
                        selectedMechanicForBooking = mech
                    }
                )
                Screen.Bookings -> BookingsScreen(
                    bookings = bookings,
                    userRole = userRole,
                    onCancelBooking = { bookingId ->
                        coroutineScope.launch {
                            repository.updateBookingStatus(bookingId, "cancelled")
                            Toast.makeText(context, "SOS $bookingId Cancelled", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onUpdateStatus = { bookingId, newStatus, finalPrice ->
                        coroutineScope.launch {
                            repository.updateBookingStatus(bookingId, newStatus, finalPrice)
                            Toast.makeText(context, "Status: ${newStatus.uppercase()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                Screen.Profile, Screen.MechanicTerminal -> ProfileScreen(
                    userProfile = userProfile,
                    mechanicProfile = mechanicProfile,
                    activeRole = userRole,
                    onRoleChange = { userRole = it },
                    onToggleOnline = { isOnline ->
                        userProfile?.uid?.let { uid ->
                            coroutineScope.launch {
                                repository.updateMechanicOnlineStatus(uid, isOnline)
                            }
                        }
                    },
                    onSignOut = {
                        repository.signOut()
                        userProfile = null
                        mechanicProfile = null
                        Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                    },
                    onOpenAuth = { authModalOpen = true }
                )
            }

            // Booking Confirmation Sheet Dialog
            selectedMechanicForBooking?.let { mech ->
                val estimatedFee = when {
                    selectedProblem.contains("Puncture", true) -> mech.pricing.punctureRepair
                    selectedProblem.contains("Jumpstart", true) -> mech.pricing.jumpstart
                    selectedProblem.contains("Towing", true) -> mech.pricing.towingBase
                    else -> mech.pricing.baseInspection + mech.pricing.hourlyLabor
                }

                AlertDialog(
                    onDismissRequest = { selectedMechanicForBooking = null },
                    containerColor = CardBackground,
                    shape = RoundedCornerShape(20.dp),
                    title = {
                        Text(
                            text = "Confirm Emergency SOS Dispatch",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Dispatching to: ${mech.garageName}",
                                color = NeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "• Vehicle: ${selectedVehicle.title}\n• Issue: $selectedProblem\n• Estimated Arrival: ~${mech.etaMinutes} mins\n• Base Estimate: ₹$estimatedFee",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val newBooking = Booking(
                                    customerId = userProfile?.uid ?: "guest-${System.currentTimeMillis() % 10000}",
                                    customerName = userProfile?.displayName ?: "Motorist",
                                    customerPhone = userProfile?.phoneNumber ?: "+91 98765 43210",
                                    mechanicId = mech.id,
                                    mechanicName = mech.name,
                                    garageName = mech.garageName,
                                    mechanicPhone = mech.phone,
                                    vehicleType = selectedVehicle,
                                    problemType = selectedProblem,
                                    estimatedPrice = estimatedFee,
                                    etaMinutes = mech.etaMinutes
                                )
                                coroutineScope.launch {
                                    val res = repository.createBooking(newBooking)
                                    selectedMechanicForBooking = null
                                    currentScreen = Screen.Bookings
                                    Toast.makeText(context, "SOS Sent! (${res.getOrDefault("BK-101")})", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("TRANSMIT SOS", color = DarkBackground, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedMechanicForBooking = null }) {
                            Text("Cancel", color = TextSecondary)
                        }
                    }
                )
            }

            // Auth Screen Modal
            if (authModalOpen) {
                AuthScreen(
                    initialRole = userRole,
                    onDismiss = { authModalOpen = false },
                    onLogin = { email, pass ->
                        coroutineScope.launch {
                            val res = repository.signInWithEmail(email, pass)
                            res.onSuccess { profile ->
                                userProfile = profile
                                userRole = profile.role
                                authModalOpen = false
                                Toast.makeText(context, "Welcome back, ${profile.displayName}!", Toast.LENGTH_SHORT).show()
                            }.onFailure {
                                Toast.makeText(context, it.message ?: "Sign in failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onRegisterCustomer = { email, pass, name, phone ->
                        coroutineScope.launch {
                            val res = repository.signUpCustomer(email, pass, name, phone)
                            res.onSuccess { profile ->
                                userProfile = profile
                                userRole = UserRole.CUSTOMER
                                authModalOpen = false
                                Toast.makeText(context, "Customer account created!", Toast.LENGTH_SHORT).show()
                            }.onFailure {
                                Toast.makeText(context, it.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onRegisterMechanic = { email, pass, name, gName, phone, area, vehicles, pricing ->
                        coroutineScope.launch {
                            val res = repository.signUpMechanic(email, pass, name, gName, phone, area, vehicles, pricing)
                            res.onSuccess { profile ->
                                userProfile = profile
                                userRole = UserRole.MECHANIC
                                authModalOpen = false
                                Toast.makeText(context, "Garage Terminal registered!", Toast.LENGTH_SHORT).show()
                            }.onFailure {
                                Toast.makeText(context, it.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
    }
}
