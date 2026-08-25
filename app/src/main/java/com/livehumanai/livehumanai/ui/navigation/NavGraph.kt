package com.livehumanai.livehumanai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.livehumanai.livehumanai.ui.HomeScreen
import com.livehumanai.livehumanai.ui.chat.ChatScreen
import com.livehumanai.livehumanai.ui.diagnostics.DiagnosticsScreen
import com.livehumanai.livehumanai.ui.jalebi.JalebiDeveloperScreen
import com.livehumanai.livehumanai.ui.memory.MemoryScreen
import com.livehumanai.livehumanai.ui.models.ModelManagerScreen
import com.livehumanai.livehumanai.ui.performance.PerformanceScreen
import com.livehumanai.livehumanai.ui.privacy.PrivacyScreen
import com.livehumanai.livehumanai.ui.settings.SettingsScreen
import com.livehumanai.livehumanai.ui.vision.VisionScreen

@Composable fun NavGraph(){val nav=rememberNavController();NavHost(nav,Screen.Home.route){composable(Screen.Home.route){HomeScreen({nav.navigate(Screen.Chat.route)},{nav.navigate(Screen.Vision.route)},{nav.navigate(Screen.Settings.route)},{nav.navigate(Screen.Jalebi.route)})};composable(Screen.Chat.route){ChatScreen()};composable(Screen.Vision.route){VisionScreen()};composable(Screen.Models.route){ModelManagerScreen()};composable(Screen.Settings.route){SettingsScreen{nav.popBackStack()}};composable(Screen.Performance.route){PerformanceScreen()};composable(Screen.Memory.route){MemoryScreen()};composable(Screen.Diagnostics.route){DiagnosticsScreen()};composable(Screen.Privacy.route){PrivacyScreen()};composable(Screen.Jalebi.route){JalebiDeveloperScreen()}}}
sealed class Screen(val route:String){object Home:Screen("home");object Chat:Screen("chat");object Vision:Screen("vision");object Models:Screen("models");object Settings:Screen("settings");object Performance:Screen("performance");object Memory:Screen("memory");object Diagnostics:Screen("diagnostics");object Privacy:Screen("privacy");object Jalebi:Screen("jalebi-developer")}
