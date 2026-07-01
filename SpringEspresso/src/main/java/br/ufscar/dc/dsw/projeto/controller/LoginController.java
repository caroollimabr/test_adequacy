package br.ufscar.dc.dsw.projeto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Controller
public class LoginController {

    //documentacao OpenAPI
    @Operation(summary = "Página de Login")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view login")
    })
    //  
    @GetMapping("/login")
    public String login() {
        return "login"; 
    }
}
