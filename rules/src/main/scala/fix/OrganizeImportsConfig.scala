package fix

import metaconfig.Conf
import metaconfig.ConfDecoder
import metaconfig.ConfEncoder
import metaconfig.generic.Surface
import metaconfig.generic.deriveDecoder
import metaconfig.generic.deriveEncoder
import metaconfig.generic.deriveSurface
import scalafix.internal.config.ReaderUtil

sealed trait ImportsOrder

object ImportsOrder {
  case object Ascii extends ImportsOrder
  case object SymbolsFirst extends ImportsOrder
  case object Keep extends ImportsOrder

  implicit def reader: ConfDecoder[ImportsOrder] = ReaderUtil.oneOf(Ascii, SymbolsFirst, Keep)
  implicit def writer: ConfEncoder[ImportsOrder] = ConfEncoder.instance(v => Conf.Str(v.toString))
}

sealed trait ImportSelectorsOrder

object ImportSelectorsOrder {
  case object Ascii extends ImportSelectorsOrder
  case object SymbolsFirst extends ImportSelectorsOrder
  case object Keep extends ImportSelectorsOrder

  implicit def reader: ConfDecoder[ImportSelectorsOrder] =
    ReaderUtil.oneOf(Ascii, SymbolsFirst, Keep)

  implicit def writer: ConfEncoder[ImportSelectorsOrder] =
    ConfEncoder.instance(v => Conf.Str(v.toString))
}

sealed trait GroupedImports

object GroupedImports {
  case object AggressiveMerge extends GroupedImports
  case object Merge extends GroupedImports
  case object Explode extends GroupedImports
  case object Keep extends GroupedImports

  implicit def reader: ConfDecoder[GroupedImports] =
    ReaderUtil.oneOf(AggressiveMerge, Merge, Explode, Keep)

  implicit def writer: ConfEncoder[GroupedImports] =
    ConfEncoder.instance(v => Conf.Str(v.toString))
}

sealed trait BlankLines

object BlankLines {
  case object Auto extends BlankLines
  case object Manual extends BlankLines

  implicit def reader: ConfDecoder[BlankLines] = ReaderUtil.oneOf(Auto, Manual)
  implicit def writer: ConfEncoder[BlankLines] = ConfEncoder.instance(v => Conf.Str(v.toString))
}

sealed trait Preset

object Preset {
  case object DEFAULT extends Preset
  case object INTELLIJ_2020_3 extends Preset

  implicit def reader: ConfDecoder[Preset] = ReaderUtil.oneOf(DEFAULT, INTELLIJ_2020_3)
  implicit def writer: ConfEncoder[Preset] = ConfEncoder.instance(v => Conf.Str(v.toString))
}

final case class OrganizeImportsConfig(
  blankLines: BlankLines = BlankLines.Auto,
  coalesceToWildcardImportThreshold: Int = Int.MaxValue,
  expandRelative: Boolean = false,
  groupExplicitlyImportedImplicitsSeparately: Boolean = false,
  groupedImports: GroupedImports = GroupedImports.Explode,
  groups: Seq[String] = Seq(
    "re:javax?\\.",
    "scala.",
    "*"
  ),
  importSelectorsOrder: ImportSelectorsOrder = ImportSelectorsOrder.Ascii,
  importsOrder: ImportsOrder = ImportsOrder.Ascii,
  preset: Preset = Preset.DEFAULT,
  removeUnused: Boolean = true
)

object OrganizeImportsConfig {
  val default: OrganizeImportsConfig = OrganizeImportsConfig()

  implicit val surface: Surface[OrganizeImportsConfig] =
    deriveSurface[OrganizeImportsConfig]

  implicit val decoder: ConfDecoder[OrganizeImportsConfig] =
    deriveDecoder[OrganizeImportsConfig](default)

  implicit val encoder: ConfEncoder[OrganizeImportsConfig] =
    deriveEncoder[OrganizeImportsConfig]

  val presets: Map[Preset, OrganizeImportsConfig] = Map(
    Preset.DEFAULT -> OrganizeImportsConfig(),
    Preset.INTELLIJ_2020_3 -> OrganizeImportsConfig(
      blankLines = BlankLines.Manual,
      coalesceToWildcardImportThreshold = 5,
      groupedImports = GroupedImports.Merge,
      groups = Seq(
        "*",
        "---",
        "java.",
        "javax.",
        "scala."
      )
    )
  )
}
