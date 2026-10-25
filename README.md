# MiniPix 💸

Mini banco no terminal, feito em **Java**, com contas, login por senha, chaves Pix e transferências entre contas.

Projeto de estudo para praticar orientação a objetos, coleções e organização de código.

## O que ele faz

- Login por conta, com criação de senha no primeiro acesso e limite de 3 tentativas
- Transferência por **chave Pix**, com validação de chave, valor e saldo
- Cadastro de novas chaves (uma chave não pode ter dois donos)
- Histórico de **recebidos**, mostrando de quem veio cada valor
- Troca de conta sem fechar o programa
- Cada conta é independente: ninguém vê o saldo ou os recebidos de outra conta
- Menu interativo com cabeçalho (usuário, tipo de conta e saldo) e animação de "Processando..."

## Como rodar

Precisa do **JDK 21** instalado (funciona também em versões mais antigas com `record`, a partir do 16).

```bash
javac Main.java
java Main
```

Contas de teste: `P` (crédito) e `Clara` (débito). No primeiro acesso, o programa pede para criar a senha.

## O que eu pratiquei

- Classes e objetos (`Conta`) e `record` (`Transacao`)
- `Map` para ligar chave Pix → conta e nome → conta
- `List` para guardar o histórico de cada conta
- Dinheiro em **centavos (`long`)** para evitar erros de arredondamento do `double`
- Métodos pequenos e reutilizáveis (`transferir`, `cadastrarChave`, `entrar`)
- Validação de entrada e regras de negócio

## Limitações conhecidas

- Os dados ficam só na memória: ao fechar, tudo volta ao estado inicial
- A senha é guardada em texto puro (nada de hash ainda)
- Ainda é possível enviar dinheiro para a própria chave

## Próximos passos

- [ ] Bloquear transferência para si mesmo
- [ ] Guardar a senha com hash em vez de texto puro
- [ ] Salvar contas e histórico em arquivo
- [ ] Histórico de enviados, além dos recebidos
- [ ] Testes automatizados com JUnit
