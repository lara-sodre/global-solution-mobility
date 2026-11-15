# Global Solution 2026-02 — Acessibilidade Inclusiva: Mobilidade sem barreiras

Microsserviços com **comunicação síncrona** (Java 25 + Spring Boot 4.0.9 + MySQL).

## 👥 Integrantes

| Nome | RM |
|------|----|
| NOME COMPLETO DO INTEGRANTE 1 | RM00000 |
| NOME COMPLETO DO INTEGRANTE 2 | RM00000 |

## 🎯 Problema

A acessibilidade urbana ainda tem muitas barreiras para pessoas com deficiência permanente. A falta de
informação sobre a estrutura de estabelecimentos, calçadas, transportes e espaços públicos limita a
autonomia e a segurança dessas pessoas. Além disso, **acessível para uma pessoa não significa acessível para
todas**: um local com rampa serve a quem usa cadeira de rodas, mas pode não ter piso tátil para quem tem
deficiência visual.

## 💡 Solução

Plataforma de acessibilidade **personalizada e colaborativa**. Esta entrega implementa o núcleo da plataforma
em dois microsserviços:

| Microsserviço | Responsabilidade |
|---------------|------------------|
| **ms-locais** | Mapeia locais (estabelecimentos/espaços públicos) e seus **recursos de acessibilidade por tipo de deficiência** (`FISICA`, `VISUAL`, `AUDITIVA`, `INTELECTUAL`), informando se cada recurso está disponível. |
| **ms-avaliacoes** | Recebe **avaliações colaborativas** sobre as condições reais de acessibilidade de um local, indicando o tipo de deficiência do avaliador, nota e a barreira encontrada. |

**Comunicação síncrona:** quando uma avaliação é validada (`PATCH /api/v1/avaliacoes/{id}/validar`), o
`ms-avaliacoes` chama o `ms-locais` (OpenFeign + Eureka + Load Balancer) e o local passa de `CADASTRADO`
para `VERIFICADO`, ou seja, tem a informação confirmada pela comunidade.

Se o `ms-locais` estiver indisponível, o **Circuit Breaker + Fallback** responde `503` e a avaliação fica
`VALIDACAO_PENDENTE` (nada é perdido).

### Status

| Microsserviço | Status possíveis | Quem altera |
|---------------|------------------|-------------|
| ms-locais | `CADASTRADO` → `VERIFICADO` | **ms-avaliacoes**, via OpenFeign |
| ms-avaliacoes | `REGISTRADA` → `VALIDADA` ou `VALIDACAO_PENDENTE` (fallback) | Endpoint `/validar` |

## 🏗️ Arquitetura

```
                       ┌────────────────────┐
                       │   Eureka Server    │  :8761
                       └─────▲───────▲──────┘
             registra/descobre│       │registra/descobre
 Cliente ──► API Gateway :8082 ───┬───┴────────────┐
 (Swagger/Postman)                │                │
                      ┌───────────▼──┐   OpenFeign  ┌▼────────────┐
                      │ ms-avaliacoes│ ───────────► │  ms-locais  │   (porta aleatória,
                      │ (n instâncias)│ LoadBalancer │(n instâncias)│    várias instâncias)
                      └──────┬───────┘ CircuitBreaker└──────┬──────┘
                             │          + Fallback          │
                    MySQL :3307 (db_avaliacoes)    MySQL :3306 (db_locais)
```

Camadas de cada microsserviço: `controller` → `service` → `repository` → `entities`, com `dto` (Request/Response),
`exceptions` (exceções customizadas + `GlobalExceptionHandler`), `client` (Feign) e `config`.

## ✅ Requisitos

- **Java 25**
- **Docker** e Docker Compose (somente para o MySQL)
- Maven não precisa estar instalado: usa-se o `mvnw` de cada projeto
- Portas livres: `8761` (Eureka), `8082` (Gateway), `3306` e `3307` (MySQL)

## ▶️ Como executar (ambiente limpo)

Execute cada passo em um **terminal separado**, na ordem abaixo, a partir da raiz do repositório.
No Windows use `mvnw.cmd` no lugar de `./mvnw` (no Linux/Mac, se necessário: `chmod +x */mvnw`).

**1) Subir os dois bancos MySQL (Docker)**

```bash
docker compose up -d
docker compose ps        # aguarde os dois containers ficarem "healthy"
```

> `mysql-locais` → `localhost:3306` (`db_locais`) · `mysql-avaliacoes` → `localhost:3307` (`db_avaliacoes`) · usuário `root` / senha `root`.
> As tabelas e a carga inicial são criadas automaticamente pelo **Flyway** na primeira execução.

**2) Eureka Server** (porta 8761)

```bash
cd eureka-server
./mvnw spring-boot:run
```

**3) ms-locais** (porta aleatória)

```bash
cd ms-locais
./mvnw spring-boot:run
```

**4) ms-avaliacoes** (porta aleatória)

```bash
cd ms-avaliacoes
./mvnw spring-boot:run
```

**5) API Gateway** (porta 8082)

```bash
cd api-gateway
./mvnw spring-boot:run
```

**Múltiplas instâncias:** repita o passo 3 e/ou 4 em outro terminal. Como `server.port=0`, cada instância
sobe em uma porta aleatória e aparece separadamente no Eureka. O Load Balancer distribui as chamadas.

> Também é possível executar cada projeto pelo IntelliJ IDEA (classes `*Application`). Para várias instâncias,
> habilite *Allow multiple instances* na configuração de execução.

## 🔗 URLs

| O quê | URL |
|-------|-----|
| Eureka (dashboard) | http://localhost:8761 |
| **Swagger UI (único, via Gateway)** | http://localhost:8082/swagger-ui.html |
| ms-locais via Gateway | http://localhost:8082/ms-locais/api/v1/locais |
| ms-avaliacoes via Gateway | http://localhost:8082/ms-avaliacoes/api/v1/avaliacoes |

Swagger direto em uma instância: `http://localhost:<porta-do-Eureka>/swagger-ui.html` (selecione o servidor `/` na lista *Servers*).

No Swagger UI do Gateway, escolha **ms-locais** ou **ms-avaliacoes** na lista (canto superior direito).
A lista é atualizada a cada 10 s; se estiver vazia logo após subir, aguarde alguns segundos e recarregue.

## 📚 Endpoints (API v1)

### ms-locais — `/api/v1/locais`

| Método | Rota | Descrição | Retorno |
|--------|------|-----------|---------|
| GET | `/api/v1/locais` | Lista locais | 200 |
| GET | `/api/v1/locais/{id}` | Busca por ID | 200 / 404 |
| POST | `/api/v1/locais` | Cadastra local (status `CADASTRADO`) | 201 + `Location` / 422 |
| PUT | `/api/v1/locais/{id}` | Atualiza local | 200 / 404 / 409 (já `VERIFICADO`) / 422 |
| PUT | `/api/v1/locais/{localId}/avaliacao/validada` | Marca como `VERIFICADO` (**chamado via Feign**) | 204 / 404 |
| DELETE | `/api/v1/locais/{id}` | Exclui | 204 / 404 |

### ms-avaliacoes — `/api/v1/avaliacoes`

| Método | Rota | Descrição | Retorno |
|--------|------|-----------|---------|
| GET | `/api/v1/avaliacoes` | Lista avaliações | 200 |
| GET | `/api/v1/avaliacoes/{id}` | Busca por ID | 200 / 404 |
| POST | `/api/v1/avaliacoes` | Registra avaliação (status `REGISTRADA`) | 201 + `Location` / 422 |
| PUT | `/api/v1/avaliacoes/{id}` | Atualiza avaliação | 200 / 404 / 409 (já `VALIDADA`) / 422 |
| PATCH | `/api/v1/avaliacoes/{id}/validar` | Valida e altera o local no ms-locais (**Feign + Circuit Breaker**) | 200 / 404 / **503** (fallback) |
| DELETE | `/api/v1/avaliacoes/{id}` | Exclui | 204 / 404 |

## 🧪 Roteiro de teste

Todos os exemplos passam pelo Gateway (`localhost:8082`). Se preferir, faça tudo pelo Swagger UI.

**1. Ver os dados iniciais (Flyway)**

```bash
curl http://localhost:8082/ms-locais/api/v1/locais
curl http://localhost:8082/ms-avaliacoes/api/v1/avaliacoes
```

**2. Cadastrar um local**

```bash
curl -i -X POST http://localhost:8082/ms-locais/api/v1/locais \
  -H "Content-Type: application/json" \
  -d '{
        "nome": "Cinema Central",
        "endereco": "Rua do Cinema, 45",
        "cidade": "São Paulo",
        "tipoLocal": "CINEMA",
        "recursos": [
          { "descricao": "Espaço reservado para cadeira de rodas", "tipoDeficiencia": "FISICA", "disponivel": true, "localizacao": "Sala 1", "observacao": "Dois espaços por sessão" },
          { "descricao": "Sessões com audiodescrição", "tipoDeficiencia": "VISUAL", "disponivel": true, "localizacao": "Todas as salas", "observacao": null },
          { "descricao": "Sessões com legendas", "tipoDeficiencia": "AUDITIVA", "disponivel": false, "localizacao": "Todas as salas", "observacao": "Previsto para o próximo mês" }
        ]
      }'
```

**3. Registrar uma avaliação para o local 1** (status `REGISTRADA`)

```bash
curl -i -X POST http://localhost:8082/ms-avaliacoes/api/v1/avaliacoes \
  -H "Content-Type: application/json" \
  -d '{
        "nomeUsuario": "João Pereira",
        "tipoDeficiencia": "FISICA",
        "nota": 5,
        "comentario": "Rampa na entrada e corredores largos.",
        "barreiraEncontrada": null,
        "localId": 1
      }'
```

**4. Validar a avaliação → altera o status do local (comunicação síncrona)**

```bash
curl -i -X PATCH http://localhost:8082/ms-avaliacoes/api/v1/avaliacoes/1/validar
curl http://localhost:8082/ms-locais/api/v1/locais/1      # status agora é VERIFICADO
```

**5. Testar o Circuit Breaker + Fallback**

1. Pare **todas** as instâncias do `ms-locais` (Ctrl+C).
2. Chame novamente `PATCH /api/v1/avaliacoes/2/validar` → resposta **503** e a avaliação fica `VALIDACAO_PENDENTE`.
3. Suba o `ms-locais`, aguarde ~30 s (registro no Eureka / circuito fechar) e repita o `PATCH` → **200** e `VALIDADA`.

**6. Testar o Load Balancer:** suba duas instâncias do `ms-locais`, confirme as duas no Eureka e
chame o endpoint `/validar` várias vezes: as chamadas são distribuídas entre as instâncias.

**7. Validações / exceções:** envie um POST com campos inválidos (ex.: `"tipoDeficiencia": "XYZ"`)
→ **422** com a lista de campos; `GET /api/v1/locais/999` → **404**.

## 🗂️ Estrutura do repositório

```
.
├── docker-compose.yml        # MySQL (um container por microsserviço)
├── eureka-server/            # Service discovery (8761)
├── api-gateway/              # API Gateway + Swagger agregado (8082)
├── ms-locais/                # Microsserviço 1 (porta aleatória, db_locais)
│   └── src/main/java/br/com/fiap/ms/locais/
│       ├── client  config  controller  dto  entities
│       └── exceptions  repository  service
└── ms-avaliacoes/            # Microsserviço 2 (porta aleatória, db_avaliacoes)
    └── src/main/java/br/com/fiap/ms/avaliacoes/
        ├── client  config  controller  dto  entities
        └── exceptions  repository  service
```

## 🛠️ Tecnologias

Java 25 · Spring Boot 4.0.9 · Spring Cloud 2025.1 (Eureka, OpenFeign, LoadBalancer, Gateway, Circuit Breaker
com Resilience4j) · Spring Data JPA · Flyway · MySQL 8.4 · springdoc-openapi (Swagger UI) · Lombok · Docker.

## 🧹 Parar / limpar tudo

```bash
docker compose down -v     # remove containers e volumes (recomeça do zero)
```
