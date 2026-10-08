$dtoDir = "d:\E-Commerce\backend\src\main\java\com\ecommerce\dto"
New-Item -ItemType Directory -Force -Path $dtoDir | Out-Null

$classes = @(
    @{ Name = "LoginRequest"; Content = "package com.ecommerce.dto;`nimport lombok.Data;`nimport jakarta.validation.constraints.NotBlank;`n@Data`npublic class LoginRequest {`n    @NotBlank`n    private String email;`n    @NotBlank`n    private String password;`n}" },
    @{ Name = "SignupRequest"; Content = "package com.ecommerce.dto;`nimport lombok.Data;`nimport jakarta.validation.constraints.NotBlank;`nimport jakarta.validation.constraints.Email;`n@Data`npublic class SignupRequest {`n    @NotBlank`n    private String name;`n    @NotBlank`n    @Email`n    private String email;`n    @NotBlank`n    private String password;`n}" },
    @{ Name = "JwtResponse"; Content = "package com.ecommerce.dto;`nimport lombok.AllArgsConstructor;`nimport lombok.Data;`nimport java.util.List;`n@Data`n@AllArgsConstructor`npublic class JwtResponse {`n    private String token;`n    private Long id;`n    private String email;`n    private String name;`n    private List<String> roles;`n}" },
    @{ Name = "MessageResponse"; Content = "package com.ecommerce.dto;`nimport lombok.AllArgsConstructor;`nimport lombok.Data;`n@Data`n@AllArgsConstructor`npublic class MessageResponse {`n    private String message;`n}" },
    @{ Name = "CartRequest"; Content = "package com.ecommerce.dto;`nimport lombok.Data;`n@Data`npublic class CartRequest {`n    private Long productVariantId;`n    private int quantity;`n}" }
)

foreach ($class in $classes) {
    $path = "$dtoDir\$($class.Name).java"
    Set-Content -Path $path -Value $class.Content -Encoding UTF8
}
