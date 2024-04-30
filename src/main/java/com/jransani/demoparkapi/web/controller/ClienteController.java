package com.jransani.demoparkapi.web.controller;

import com.jransani.demoparkapi.entity.Cliente;
import com.jransani.demoparkapi.jwt.JwtUserDetails;
import com.jransani.demoparkapi.repository.projection.ClienteProjection;
import com.jransani.demoparkapi.service.ClienteService;
import com.jransani.demoparkapi.service.UsuarioService;
import com.jransani.demoparkapi.web.dto.ClienteCreateDto;
import com.jransani.demoparkapi.web.dto.ClienteResponseDto;
import com.jransani.demoparkapi.web.dto.PageableDto;
import com.jransani.demoparkapi.web.dto.UsuarioResponseDto;
import com.jransani.demoparkapi.web.dto.mapper.ClienteMapper;
import com.jransani.demoparkapi.web.dto.mapper.PageableMapper;
import com.jransani.demoparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Clientes", description = "contem todas as operações relativas ao recurso de um cliente")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    @Operation(summary = "Criar um novo cliente", description = "Recursos para criar um novo cliente vinculado a um usuário cadastrado. " +
            "Rquisição exige o uso de um bearer token. Acesso restrito a Role ='CLIENTE'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com suceso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "Cliente CPF já possui cadastrado no sistema",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por dados de entrada invalidos",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de ADMIN",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> create(@RequestBody @Valid ClienteCreateDto dto,
                                                     @AuthenticationPrincipal JwtUserDetails userDetails){
        Cliente cliente = ClienteMapper.toCliente(dto);
        cliente.setUsuario(usuarioService.buscarPorId(userDetails.getId()));
        clienteService.salvar(cliente);
        return ResponseEntity.status(201).body(ClienteMapper.toDto(cliente));
    }

    @Operation(summary = "Localizar um cliente", description = "Recursos para localizar um cliente pelo ID. " +
            "Rquisição exige o uso de um bearer token. Acesso restrito a Role ='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com suceso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDto> getById(@PathVariable Long id){
        Cliente cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }

    @Operation(summary = "Recuperar lista de clientes ",
    description = "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN' ",
    security = @SecurityRequirement(name = "security"),
    parameters = {
            @Parameter(in = QUERY, name = "page",
                    content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                    description = "representa a página retornada"
            ),
            @Parameter(in = QUERY, name = "size",
                    content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                    description = "representa o total de elementos por página"
            ),
            @Parameter(in = QUERY, name = "sort", hidden = true,
                    array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "id, asc")),
                    description = "Representa a ordenação dos resultados. Aceita multplios critérios de ordenação.")
            },
            responses = {
            @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                            schema = @Schema(implementation = ClienteController.class))
            ),
            @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                            schema = @Schema(implementation = ClienteController.class))
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> geAll(@Parameter(hidden = true) @PageableDefault(size = 5, sort = {"nome"}) Pageable pageable){
        Page<ClienteProjection> clientes = clienteService.buscarTodos(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(clientes));
    }

    @Operation(summary = "Recuperar dados do cliente autenticado ",
            description = "Requisição exige uso de um bearer token. Acesso restrito a Role='CLIENTE' ",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ClienteController.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de ADMIN",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ClienteController.class))
                    )
            })
    @GetMapping("/detalhes")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> geDetalhes(@AuthenticationPrincipal JwtUserDetails userDetails){
        Cliente cliente = clienteService.buscarPorUsuarioId(userDetails.getId());
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }

}
