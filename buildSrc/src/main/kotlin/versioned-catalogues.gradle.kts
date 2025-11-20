versionCatalogs {
    entries[project] = ForwardingVersionCatalog(
        named("libs${project.name}"),
        named("libs")
    )
}