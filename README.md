# TeleFilmes

📺 Organizador de vídeos do Telegram por séries, temporadas e episódios.

## 📱 Sobre o App

TeleFilmes é um aplicativo Android que permite organizar vídeos do Telegram de forma estruturada, agrupando-os em:
- **Séries**: Coleções principais de conteúdo
- **Temporadas**: Divisões dentro de cada série
- **Episódios**: Vídeos individuais salvos do Telegram

## ✨ Funcionalidades

- ✅ Criar e gerenciar séries personalizadas
- ✅ Organizar temporadas dentro de cada série
- ✅ Salvar vídeos do Telegram como episódios
- ✅ Autenticação com Telegram via número de telefone
- ✅ Navegar pelos chats do Telegram
- ✅ Interface moderna com Material Design 3
- ✅ Banco de dados local com Room

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Arquitetura**: MVVM (Model-View-ViewModel)
- **Banco de Dados**: Room
- **Navegação**: Navigation Compose
- **Telegram**: TDLib (Telegram Database Library)
- **Corrotinas**: Kotlin Coroutines + Flow

## 📋 Pré-requisitos

- Android Studio Arctic Fox ou superior
- JDK 17
- Android SDK 24 ou superior
- Gradle 8.2

## 🚀 Como Compilar

1. Clone o repositório:
```bash
git clone https://github.com/deivid22srk/TeleFilmes.git
cd TeleFilmes
```

2. Abra o projeto no Android Studio

3. Sincronize o Gradle:
```bash
./gradlew build
```

4. Execute no emulador ou dispositivo físico

## 📦 Build via GitHub Actions

O projeto está configurado com CI/CD usando GitHub Actions. A cada push na branch `main`, o APK debug é gerado automaticamente.

Para baixar o APK gerado:
1. Vá em **Actions** no GitHub
2. Selecione o workflow mais recente
3. Baixe o artifact **TeleFilmes-Debug-APK**

## 🔑 Configuração do Telegram

⚠️ **IMPORTANTE**: A versão atual usa uma **implementação mock** do cliente Telegram com dados de exemplo.

### Por que Mock?
A biblioteca oficial TDLib (`org.drinkless:tdlib`) não está disponível no Maven Central. Para o app funcionar de verdade com o Telegram, você precisa:

### Opção 1: Usar TDLib Oficial (Recomendado)
1. **Compilar TDLib** a partir do código-fonte:
   - Clone: https://github.com/tdlib/td
   - Siga as instruções para Android
   - Copie os arquivos `.so` para `app/src/main/jniLibs/`

2. **Adicionar dependência** (se disponível localmente):
   ```kotlin
   // Em app/build.gradle.kts
   implementation(files("libs/tdlib.jar"))
   ```

3. **Descomentar código** em:
   - `TeleFilmesApplication.kt`: `System.loadLibrary("tdjni")`
   - Restaurar implementação completa em `TelegramClient.kt`

### Opção 2: Usar Telegram Bot API
Implementar usando a API REST do Telegram (mais simples, mas com limitações)

### Opção 3: Usar Implementação Mock (Atual)
O app funciona com dados de exemplo para demonstração da UI

## 📱 Como Usar

1. **Primeira vez**: Faça login com seu número do Telegram
2. **Criar Série**: Na tela inicial, clique no botão "+"
3. **Adicionar Temporadas**: Entre na série e adicione temporadas
4. **Salvar Vídeos**: 
   - Clique no botão de chat
   - Navegue até o chat desejado
   - Selecione vídeos e salve na temporada desejada

## 📂 Estrutura do Projeto

```
app/
├── data/
│   ├── dao/           # Data Access Objects
│   ├── database/      # Room Database
│   ├── model/         # Modelos de dados
│   └── repository/    # Repositórios
├── telegram/          # Integração com Telegram
├── ui/
│   ├── navigation/    # Navegação
│   ├── screen/        # Telas Compose
│   ├── theme/         # Temas Material 3
│   └── viewmodel/     # ViewModels
├── MainActivity.kt
└── TeleFilmesApplication.kt
```

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para:
1. Fazer fork do projeto
2. Criar uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abrir um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## 👨‍💻 Autor

Desenvolvido por [@deivid22srk](https://github.com/deivid22srk)

## 🐛 Problemas Conhecidos

- ✅ **Build Funcionando**: O app compila com implementação mock
- ⚠️ **TDLib Mock**: Atualmente usando dados de exemplo, não conecta ao Telegram real
- 📱 **Funcionalidade Limitada**: Salvar vídeos funciona apenas com IDs mock
- 🔧 **Integração Real**: Requer adicionar TDLib manualmente (veja seção acima)

## 🔮 Roadmap

- [ ] Reprodutor de vídeo integrado
- [ ] Download de vídeos para offline
- [ ] Suporte a filmes (sem temporadas)
- [ ] Pesquisa e filtros
- [ ] Temas personalizados
- [ ] Backup e restauração
- [ ] Widget para tela inicial

## 📞 Suporte

Para suporte, abra uma issue no GitHub ou entre em contato através das issues do projeto.
