/*
 * Copyright 2020 Shreyas Patil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.shreyaspatil.noty.composeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import dev.shreyaspatil.noty.composeapp.component.dialog.FailureDialog
import dev.shreyaspatil.noty.composeapp.component.dialog.LoaderDialog
import dev.shreyaspatil.noty.composeapp.component.text.ConfirmPasswordTextField
import dev.shreyaspatil.noty.composeapp.component.text.PasswordTextField
import dev.shreyaspatil.noty.composeapp.component.text.TextFieldValue.Valid
import dev.shreyaspatil.noty.composeapp.component.text.UsernameTextField
import dev.shreyaspatil.noty.composeapp.navigation.NOTY_NAV_HOST_ROUTE
import dev.shreyaspatil.noty.composeapp.ui.Screen
import dev.shreyaspatil.noty.composeapp.ui.theme.typography
import dev.shreyaspatil.noty.core.ui.UIDataState
import dev.shreyaspatil.noty.view.viewmodel.RegisterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel
) {

    val viewState = viewModel.authFlow.collectAsState(initial = null).value

    when (viewState) {
        is UIDataState.Loading -> LoaderDialog()
        is UIDataState.Failed -> FailureDialog(viewState.message)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        item {
            ConstraintLayout() {

                val (
                    titleRef,
                    usernameRef,
                    passwordRef,
                    confirmPasswordRef,
                    buttonSignupRef,
                    textLoginRef,
                ) = createRefs()

                Text(
                    text = "Create\naccount",
                    style = typography.h4,
                    modifier = Modifier.constrainAs(titleRef) {
                        top.linkTo(parent.top, margin = 60.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                    }
                )

                var username by remember { mutableStateOf("") }
                var isValidUsername by remember { mutableStateOf(false) }

                UsernameTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp)
                        .constrainAs(usernameRef) {
                            top.linkTo(titleRef.bottom, margin = 30.dp)
                        }
                        .background(MaterialTheme.colors.background),
                    value = username,
                    onTextChange = {
                        username = it.data
                        isValidUsername = it is Valid
                    }
                )

                var password by remember { mutableStateOf("") }
                var isValidPassword by remember { mutableStateOf(false) }

                PasswordTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp)
                        .constrainAs(passwordRef) {
                            top.linkTo(usernameRef.bottom, margin = 16.dp)
                        }
                        .background(MaterialTheme.colors.background),
                    value = password,
                    onTextChange = {
                        password = it.data
                        isValidPassword = it is Valid
                    }
                )

                var confirmPassword by remember { mutableStateOf("") }
                var isValidConfirmPassword by remember { mutableStateOf(false) }

                ConfirmPasswordTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp)
                        .constrainAs(confirmPasswordRef) {
                            top.linkTo(passwordRef.bottom, margin = 16.dp)
                        }
                        .background(MaterialTheme.colors.background),
                    value = confirmPassword,
                    expectedValue = password,
                    onTextChange = {
                        confirmPassword = it.data
                        isValidConfirmPassword = it is Valid
                    }
                )

                Button(
                    onClick = {
                        if (isValidUsername && isValidPassword && isValidConfirmPassword) {
                            viewModel.register(username, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(16.dp, 0.dp, 16.dp, 0.dp)
                        .constrainAs(buttonSignupRef) {
                            top.linkTo(confirmPasswordRef.bottom, margin = 40.dp)
                        },
                ) {
                    Text(style = typography.subtitle1, color = Color.White, text = "Create account")
                }
                Text(
                    text = buildAnnotatedString {
                        append("Already have an account? Login")
                        addStyle(SpanStyle(color = MaterialTheme.colors.primary), 24, this.length)
                    },
                    style = typography.subtitle1,
                    modifier = Modifier
                        .constrainAs(textLoginRef) {
                            top.linkTo(buttonSignupRef.bottom, margin = 24.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                        }
                        .clickable(
                            onClick = {
                                navController.navigateUp()
                            }
                        )
                )
            }
        }
    }

    LaunchedEffect(viewState?.isSuccess) {
        if (viewState?.isSuccess == true) {
            navController.navigate(Screen.Notes.route) {
                launchSingleTop = true
                popUpTo(NOTY_NAV_HOST_ROUTE)
            }
        }
    }
}
