$ErrorActionPreference = 'Stop'
$baseUrl = "http://localhost:8081/api"
$mysqlCmd = "mysql -u root -peswar2310 e_commerce -e"

Write-Host "3. Login to get JWT"
$loginReq = @{
    email = "admin99@example.com"
    password = "password"
} | ConvertTo-Json
$loginRes = Invoke-RestMethod -Uri "$baseUrl/auth/signin" -Method Post -Body $loginReq -ContentType "application/json"
$token = $loginRes.token
$headers = @{ Authorization = "Bearer $token" }

Write-Host "4. Create Category"
$categoryReq = @{
    name = "Test Category"
    description = "Test Desc"
} | ConvertTo-Json
$categoryRes = Invoke-RestMethod -Uri "$baseUrl/categories" -Method Post -Body $categoryReq -Headers $headers -ContentType "application/json"
$categoryId = $categoryRes.id

Write-Host "5. Create Product"
$productReq = @{
    name = "Test Product"
    description = "Test Product Desc"
    basePrice = 100.00
    categoryId = $categoryId
    variants = @(
        @{
            sku = "SKU1"
            size = "M"
            color = "Red"
            price = 100.00
            stockQuantity = 10
        }
    )
} | ConvertTo-Json -Depth 5
$productRes = Invoke-RestMethod -Uri "$baseUrl/products" -Method Post -Body $productReq -Headers $headers -ContentType "application/json"
$productId = $productRes.id
$variantId = $productRes.variants[0].id

Write-Host "6. Update Variant Pricing"
$pricingReq = @{
    mrp = 150.00
    sellingPrice = 90.00
    discountType = "FLAT"
    discountValue = 60.00
    discountStartAt = (Get-Date).AddDays(-1).ToString("yyyy-MM-ddTHH:mm:ss")
    discountEndAt = (Get-Date).AddDays(5).ToString("yyyy-MM-ddTHH:mm:ss")
} | ConvertTo-Json
$pricingRes = Invoke-RestMethod -Uri "$baseUrl/products/$productId/variants/$variantId/pricing" -Method Put -Body $pricingReq -Headers $headers -ContentType "application/json"
Write-Host "Pricing Updated! Savings: $($pricingRes.savingsAmount)"

Write-Host "7. Get Deals"
$dealsRes = Invoke-RestMethod -Uri "$baseUrl/products/deals" -Method Get
Write-Host "Found Deals: $($dealsRes.Count)"

Write-Host "8. Create Coupon"
$couponReq = @{
    code = "TEST20"
    discountPercentage = 20.00
    maxDiscountAmount = 50.00
    validFrom = (Get-Date).AddDays(-1).ToString("yyyy-MM-ddTHH:mm:ss")
    validUntil = (Get-Date).AddDays(5).ToString("yyyy-MM-ddTHH:mm:ss")
    active = $true
} | ConvertTo-Json
Invoke-RestMethod -Uri "$baseUrl/coupons" -Method Post -Body $couponReq -Headers $headers -ContentType "application/json" | Out-Null

Write-Host "9. Add to Cart & Create Address"
$cartItemReq = @{
    variantId = $variantId
    quantity = 2
} | ConvertTo-Json
Invoke-RestMethod -Uri "$baseUrl/cart/add" -Method Post -Body $cartItemReq -Headers $headers -ContentType "application/json" | Out-Null

$addressReq = @{
    street = "123 Test St"
    city = "Test City"
    state = "Test State"
    country = "Test Country"
    zipCode = "12345"
} | ConvertTo-Json
$addressRes = Invoke-RestMethod -Uri "$baseUrl/addresses" -Method Post -Body $addressReq -Headers $headers -ContentType "application/json"
$addressId = $addressRes.id

Write-Host "10. Place Order with Coupon"
$orderRes = Invoke-RestMethod -Uri "$baseUrl/orders/place/$addressId`?couponCode=TEST20" -Method Post -Headers $headers -ContentType "application/json"
Write-Host "Order Placed! Total: $($orderRes.totalAmount), Discount: $($orderRes.discountAmount)"

Write-Host "11. Upload Product Image"
# Create a dummy image
Set-Content -Path "test_img.jpg" -Value "dummy image data"
$response = curl.exe -s -X POST -H "Authorization: Bearer $token" -F "file=@test_img.jpg" "$baseUrl/products/$productId/images"
Write-Host "Image Uploaded: $response"

Write-Host "All Tests Completed Successfully!"
