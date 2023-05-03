# internet-banking
Projeto ficticio de um internet banking utilizando Spring Boot e banco de dados em memória (h2).

# Cliente API

### Requisitos

1. JDK 8

### Rodando
1. Clone o projeto: `https://github.com/armandoat/internet-banking.git`
1. Entre na pasta `internet-banking` e execute: `mvnw spring-boot:run` (windows) ou `mvn spring-boot:run` (linux) 
1. Acesse: `http://localhost:8080/api/v1/contaCorrente`

### API - Funcionalidades ###
### Cadastrar um cliente
1. O cadastro do Cliente e da Conta Corrente é feito por carga através de um script sql no arquivo data.sql da pasta main/resources do projeto.

## Retornar todos os clientes
1. Endpoint: localhost:8080/api/v1/clientes/
1. Método HTTP: GET

## Sacar um valor que subtrai o saldo do cliente
1. Endpoint: localhost:8080/api/v1/contaCorrente/
1. Método HTTP: PUT
1. Request Payload: 
{
    "id": 1,
    "valor": 500.00 
}

## Depositar um valor que aumenta o saldo de um determinado cliente
1. Endpoint: localhost:8080/api/v1/contaCorrente
1. Método HTTP: POST
1. Request Payload: 
{
    "id": 1,
    "valor": 1500.00 
}

## Consultar o histórico de transações de cada movimentação por Data
(Saque e depósito)
1. Endpoint: localhost:8080/api/v1/contaCorrente/2023-05-03
1. Método HTTP: GET

## Swagger
1. Carregar a url após a subida do Spring Boot: http://localhost:8080/v3/api-docs/
1. Em seguida, carregar a url para visualizar a documentação em HTML: http://localhost:8080/swagger-ui.html
