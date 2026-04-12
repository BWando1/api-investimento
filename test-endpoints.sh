#!/bin/bash

echo "==================================================================="
echo " TESTES DOS ENDPOINTS - API INVESTIMENTO"
echo "==================================================================="
echo ""

# Obtém token
echo "[1/6] Obtendo token de autenticação..."
TOKEN_RESP=$(curl -s -X POST "http://localhost:8180/realms/investimentos/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=api-investimento&client_secret=api-secret&username=analista1&password=123456")

TOKEN=$(echo $TOKEN_RESP | grep -o '"access_token":"[^"]*"' | cut -d':' -f2 | tr -d '"')

if [ -z "$TOKEN" ]; then
  echo "❌ ERRO: Não foi possível obter o token"
  exit 1
fi

echo "✅ Token obtido"
echo ""

echo "==================================================================="
echo " TESTE 1: CDB - 12 meses - R\$ 10.000,00"
echo "==================================================================="
echo ""
echo "📋 Payload:"
echo '{"clienteId": 100, "valor": 10000.00, "prazoMeses": 12, "tipoProduto": "CDB"}'
echo ""
echo "🧮 Cálculo esperado:"
echo "   Valor Final = 10000 × (1.01)^12 = R\$ 11.268,25"
echo "   Rentabilidade Efetiva = 0.126825 (12,68%)"
echo ""
echo "📤 Resposta da API:"
curl -s -X POST "http://localhost:8080/simular-investimento" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"clienteId": 100, "valor": 10000.00, "prazoMeses": 12, "tipoProduto": "CDB"}'
echo ""
echo ""

echo "==================================================================="
echo " TESTE 2: CDB - 24 meses - R\$ 5.000,00"
echo "==================================================================="
echo ""
echo "📋 Payload:"
echo '{"clienteId": 101, "valor": 5000.00, "prazoMeses": 24, "tipoProduto": "CDB"}'
echo ""
echo "🧮 Cálculo esperado:"
echo "   Valor Final = 5000 × (1.01)^24 = R\$ 6.348,67"
echo "   Rentabilidade Efetiva = 0.269734 (26,97%)"
echo ""
echo "📤 Resposta da API:"
curl -s -X POST "http://localhost:8080/simular-investimento" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"clienteId": 101, "valor": 5000.00, "prazoMeses": 24, "tipoProduto": "CDB"}'
echo ""
echo ""

echo "==================================================================="
echo " TESTE 3: CDB - 36 meses - R\$ 20.000,00"
echo "==================================================================="
echo ""
echo "📋 Payload:"
echo '{"clienteId": 102, "valor": 20000.00, "prazoMeses": 36, "tipoProduto": "CDB"}'
echo ""
echo "🧮 Cálculo esperado:"
echo "   Valor Final = 20000 × (1.01)^36 = R\$ 28.615,38"
echo "   Rentabilidade Efetiva = 0.430769 (43,08%)"
echo ""
echo "📤 Resposta da API:"
curl -s -X POST "http://localhost:8080/simular-investimento" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"clienteId": 102, "valor": 20000.00, "prazoMeses": 36, "tipoProduto": "CDB"}'
echo ""
echo ""

echo "==================================================================="
echo " TESTE 4: CDB - 6 meses - R\$ 1.000,00"
echo "==================================================================="
echo ""
echo "📋 Payload:"
echo '{"clienteId": 103, "valor": 1000.00, "prazoMeses": 6, "tipoProduto": "CDB"}'
echo ""
echo "🧮 Cálculo esperado:"
echo "   Valor Final = 1000 × (1.01)^6 = R\$ 1.061,52"
echo "   Rentabilidade Efetiva = 0.06152 (6,15%)"
echo ""
echo "📤 Resposta da API:"
curl -s -X POST "http://localhost:8080/simular-investimento" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"clienteId": 103, "valor": 1000.00, "prazoMeses": 6, "tipoProduto": "CDB"}'
echo ""
echo ""

echo "==================================================================="
echo " TESTE 5: Produto não elegível (valor abaixo do mínimo)"
echo "==================================================================="
echo ""
echo "📋 Payload:"
echo '{"clienteId": 104, "valor": 100.00, "prazoMeses": 12, "tipoProduto": "CDB"}'
echo ""
echo "📤 Resposta da API (deve falhar - valor mínimo é R\$ 500):"
curl -s -X POST "http://localhost:8080/simular-investimento" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"clienteId": 104, "valor": 100.00, "prazoMeses": 12, "tipoProduto": "CDB"}'
echo ""
echo ""

echo "==================================================================="
echo " TESTE 6: Tipo de produto inexistente"
echo "==================================================================="
echo ""
echo "📋 Payload:"
echo '{"clienteId": 105, "valor": 10000.00, "prazoMeses": 12, "tipoProduto": "BITCOIN"}'
echo ""
echo "📤 Resposta da API (deve falhar - produto não existe):"
curl -s -X POST "http://localhost:8080/simular-investimento" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"clienteId": 105, "valor": 10000.00, "prazoMeses": 12, "tipoProduto": "BITCOIN"}'
echo ""
echo ""

echo "==================================================================="
echo " Testes concluídos!"
echo "==================================================================="
