## 在 x86 架构下打包 ARM 环境的镜像

### 0、配置 Docker buildx

首先，确保你的 Docker 配置支持构建多平台镜像。使用 `docker buildx` 需要先创建一个 buildx builder 实例并设为默认。

```bash
docker buildx create --use --name mybuilder
```

这会创建一个名为 `mybuilder` 的 builder 实例，并自动将其设为默认实例。

### 1、查看所选环境是否支持 ARM 架构

在构建 ARM 架构的镜像前，可以先检查所选基础镜像是否支持 ARM 架构。比如，你可以查看 `openjdk:17-jdk-alpine` 是否支持 ARM64 架构：

```bash
docker buildx imagetools inspect openjdk:17-jdk-alpine
```

如果输出中有 `linux/arm64`，说明这个镜像支持 ARM 架构。例如：

```bash
Manifests:
  Name:      docker.io/library/openjdk:17-jdk-alpine@sha256:a996cdcc040704ec6badaf5fecf1e144c096e00231a29188596c784bcf858d05
  MediaType: application/vnd.docker.distribution.manifest.v2+json
  Platform:  linux/amd64
  Platform:  linux/arm64
```

### 2、安装 QEMU 多平台支持（可选）

为了支持跨平台的构建，特别是 ARM 架构模拟，可以通过 Docker 安装 QEMU 支持。QEMU 使得你能够在 x86 架构上模拟 ARM 等其他架构，进行跨平台的镜像构建。

```bash
docker run --rm --privileged multiarch/qemu-user-static --reset -p yes
```

这条命令会运行一个 Docker 容器，该容器会安装并配置 QEMU 以支持多平台。安装完成后，QEMU 会与 Docker 自动集成，帮助你在 x86 机器上构建 ARM 架构的镜像。

### 3、使用 `docker buildx` 构建 ARM 镜像

一旦完成上述配置，你就可以使用 `docker buildx` 构建多平台的镜像了。

- 使用 `--platform` 参数来指定目标平台（如 `linux/arm64` 或 `linux/amd64`）。
- `--load` 表示将构建的镜像加载到本地镜像库。
- `--push` 表示将镜像推送到 Docker Hub 或其他远程仓库。

#### 构建 ARM 和 x86 镜像并推送

```bash
docker buildx build -t patricleee/cni-bbs-core:1.0 --platform linux/arm64,linux/amd64 --push .
```

这条命令会构建 `linux/arm64` 和 `linux/amd64` 两个平台的镜像，并将其推送到远程仓库。

#### 构建单独的 ARM 镜像并加载到本地

如果只想构建 ARM 架构的镜像，可以指定 `--platform linux/arm64`，并使用 `--load` 将镜像加载到本地：

```bash
docker buildx build -t patricleee/cni-bbs-core-arm:1.0 --platform linux/arm64 --load .
```

这样，构建的镜像会加载到本地镜像库，但不会推送到远程仓库。