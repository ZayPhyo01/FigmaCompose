package com.guru.processor.figma

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.guru.annotation.Figma

class FigmaProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {
    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {

        val dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray())

        val symbolAnnonation = resolver.getSymbolsWithAnnotation(
            Figma::class.qualifiedName.toString()
        )
        val symbols = symbolAnnonation.filterIsInstance(KSFunctionDeclaration::class.java)


        if (symbols.toList().isNotEmpty())
            codeGenerator.createNewFile(
                dependencies = Dependencies(false),
                packageName = symbols.first().packageName.asString(),
                fileName = "index",
                extensionName = "html"
            ).let { writer ->
                writer.write(
                    ("<html><head>" +
                            "<style>" +
                            "${navBarCssStyle()}\n" +
                            gridLayoutCssStyle() +
                            gridItemCssStyle() +
                            zoomHoverCssStyle() +
                            iFrameCssStyle() +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<div class = \"topnav\">Catalog</div><br>" +
                            "<div class = \"grid-container\">"
                            ).toByteArray(
                            Charsets.UTF_8
                        )
                )
                symbols.filter {
                    it.isAnnotationPresent(Figma::class)
                }.forEach {
                    val url = it.getAnnotationsByType(Figma::class).first().url
                    writer.write(
                        ("<div class = \"grid-item zoom\">" +
                                "<iframe style=\"border: 0px solid rgba(0, 0, 0, 0.1);\" width=\"200\" height=\"400\" src=\"https://www.figma.com/embed?embed_host=share&url=$url\"  loading=\"lazy\"></iframe>" +
                                "<p>${it.simpleName.asString()}</p>" +
                                "</div>\n").toByteArray(
                            Charsets.UTF_8
                        )
                    )
                }
                writer.write(
                    "</div></body></html>".toByteArray(
                        Charsets.UTF_8
                    )
                )
            }


        return emptyList()
    }

    private fun navBarCssStyle() = ".topnav {\n" +
            "  color: #fff;\n" +
            "  text-decoration: none;\n" +
            "  font-size: 24px;\n" +
            "  width: 100%;\n" +
            "  padding: 20px;\n" +
            "  background-color: #1D5CB7;\n" +
            "}\n"

    private fun gridLayoutCssStyle() = ".grid-container {\n" +
            "  display: grid;\n" +
            "  grid-template-columns: auto auto auto;\n" +
            "  margin-top: 40px;\n" +
            "  justify-content: center;" +
            "  row-gap: 40px;" +
            "  column-gap: 24px;" +
            "}\n"

    private fun gridItemCssStyle() = ".grid-item {\n" +
            "text-align: center;" +
            "background-color: #FFFFD0D0;" +
            "padding: 14px;" +
            "border-radius: 24px;" +
            "}\n"

    private fun zoomHoverCssStyle() = ".zoom {\n" +
            "  transition: transform .3s; /* Animation */\n" +
            "}\n" +
            "\n" +
            ".zoom:hover {\n" +
            "  transform: scale(1.02);" +
            "}"

    private fun iFrameCssStyle() = "iframe \n{" +
            "touch-action: none;\n" +
            "}"
}