
# 项目整体结构

```text
├── gradle-build-logics/       # 集中构建逻辑管理目录
│   ├── convention/            # 自定义 Gradle 插件
│   ├── gradle.properties      # 全局 Gradle 配置
│   ├── settings.gradle.kts    # 全局项目设置与仓库配置
│   └── tomls/                 # 依赖/插件版本统一管理（TOML 格式）
├── libs/                      # 通用库模块（工具类、组件等）
├── workspace-kyxd/            # 业务模块 1（客户相关功能）
├── workspace-live/            # 业务模块 2（直播功能）
├── workspace-mby/             # 业务模块 3（物联网相关功能）
├── workspace-pretimmediat/    # 业务模块 4（预即时功能）
├── workspace-reader/          # 业务模块 5（阅读功能）
├── workspace-test/            # 测试模块（包含单元测试与 UI 测试）
├── workspace-xy/              # 业务模块 6（XY 专项功能）
├── build.gradle.kts           # 全局项目构建脚本
├── gradle.properties          # 全局项目属性
├── gradlew/gradlew.bat        # Gradle 执行脚本（跨平台）
└── settings.gradle.kts        # 全局项目模块注册与依赖解析配置
```

# 核心模块详解：`gradle-build-logics`

`gradle-build-logics` 是项目的构建基础设施核心，通过统一配置、版本管理、插件封装三大机制，解决多模块项目中的依赖冲突和配置冗余问题。

## 1. `tomls` 目录：依赖版本集中管理

通过 TOML 格式文件统一维护所有依赖与插件的版本，避免模块间版本不一致导致的兼容性问题。

| 文件名称           | 功能描述                                                                 |
|--------------------|--------------------------------------------------------------------------|
| `androidx.toml`    | 管理 AndroidX 组件（如 Jetpack Compose、Room、Lifecycle）的版本。       |
| `kotlin.toml`      | 管理 Kotlin 语言本身及 Gradle 插件的版本。                              |
| `hilt.toml`        | 管理 Hilt 依赖注入框架的版本。                                           |
| `common.toml`      | 管理 Android 构建核心配置及基础库版本。                                 |
| `coil.toml`        | 管理图片加载库 Coil 的版本。                                             |
| `glide.toml`       | 管理图片加载库 Glide 的版本。                                            |
| `test.toml`        | 管理测试库的版本（如 JUnit、Espresso）。                                |

**注意事项**：
- 修改版本后需重新同步 Gradle。
- 新增依赖请在对应 TOML 文件中补充版本声明，避免硬编码。

## 2. `convention` 目录：自定义 Gradle 插件

通过封装插件统一各模块的构建配置，减少重复代码。

### `InitialConventionPlugin`
- 功能：扫描 `tomls` 文件，解析依赖并统一版本。
- 注意事项：如需排除强制控制需加入白名单。

### `AndroidApplicationConventionPlugin`
- 功能：为 Application 模块统一配置编译参数。
- 来源：参数读取自 `common.toml`。

### `AndroidLibraryConventionPlugin`
- 功能：配置库模块构建参数并限制依赖范围。
- 注意事项：库模块应避免业务耦合。

### `AndroidXHiltConventionPlugin`
- 功能：自动添加 Hilt 依赖及配置 kapt/ksp。
- 注意事项：仅在需要依赖注入的模块使用。

## 3. `settings.gradle.kts`：全局项目设置

- 优先使用 Google Maven 获取 Android 依赖。
- 使用 Maven Central 获取第三方库。
- 启用 `versionCatalogs` 功能统一依赖声明格式。

**注意事项**：
- 添加插件仓库需在 `pluginManagement.repositories` 中配置。
- 调整仓库顺序可能影响解析效率。

# 项目设计理念

- **模块化隔离**：功能模块与通用模块分离。
- **构建逻辑集中**：统一配置避免混乱。
- **依赖版本可控**：减少版本冲突风险。

# 使用建议

- **新增模块**：需注册至 `settings.gradle.kts`，并选择合适插件。
- **依赖声明**：在 `tomls` 中添加后通过 `libs.xxx` 引用。
- **配置同步**：修改后需执行 Gradle Sync。

通过以上机制，项目支持高效多人协作，降低构建维护成本，确保一致性与稳定性。
