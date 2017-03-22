// See LICENSE for license details.

package craft

import cde._
import chisel3._
import chisel3.experimental._
import chisel3.util._
import dspblocks._
import dspjunctions._
import testchipip._
import _root_.util._

trait ADCTopLevelIO {
  val ADCBIAS       = Analog(1.W)
  val adcextclk     = Input(Bool())
  val ADCINP        = Analog(1.W)
  val ADCINM        = Analog(1.W)
  val ADCCLKP       = Analog(1.W)
  val ADCCLKM       = Analog(1.W)
  val adcclkrst     = Input(Bool())
}

trait LazyADC {
  def scrbuilder: SCRBuilder

  scrbuilder.addControl("OSP")
  scrbuilder.addControl("OSM")
  scrbuilder.addControl("ASCLKD")
  scrbuilder.addControl("EXTSEL_CLK")
  scrbuilder.addControl("VREF0")
  scrbuilder.addControl("VREF1")
  scrbuilder.addControl("VREF2")
  scrbuilder.addControl("IREF")
  scrbuilder.addControl("CLKGCAL")
  scrbuilder.addControl("CLKGBIAS")
  scrbuilder.addControl("ADC_VALID")
  scrbuilder.addControl("ADC_SYNC")
}

trait LazyCAL {
  def scrbuilder: SCRBuilder

  scrbuilder.addControl("MODE")
  scrbuilder.addControl("ADDR")
  scrbuilder.addControl("WEN")
  (0 until 32).foreach { i =>
    scrbuilder.addControl(s"CALCOEFF$i")
    scrbuilder.addStatus(s"CALOUT$i")
  }
}


trait HasDspOutputClock {
  val adc_clk_out = Output(Clock())
}

trait DspChainADCIO extends ADCTopLevelIO with HasDspOutputClock

trait ADCModule {
  def io: DspChainIO with DspChainADCIO
  def scrfile: SCRFile
  def clock: Clock // module's implicit clock
  def reset: Bool

  val adc = Module(new TISARADC)

  attach(io.ADCBIAS,    adc.io.ADCBIAS)
  attach(io.ADCINP,     adc.io.ADCINP)
  attach(io.ADCINM,     adc.io.ADCINM)
  attach(io.ADCCLKP,    adc.io.ADCCLKP)
  attach(io.ADCCLKM,    adc.io.ADCCLKM)

  def wordToByteVec(u: UInt): Vec[UInt] =
    u.asTypeOf(Vec(8, UInt(8.W)))
  def wordToNibbleVec(u: UInt): Vec[UInt] =
    u.asTypeOf(Vec(16, UInt(4.W)))
  def wordToBoolVec(u: UInt): Vec[Bool] =
    u.asTypeOf(Vec(64, Bool()))

  val osp = wordToByteVec(scrfile.control("OSP"))
  val osm = wordToByteVec(scrfile.control("OSM"))
  val asclkd = wordToNibbleVec(scrfile.control("ASCLKD"))
  val extsel_clk = wordToBoolVec(scrfile.control("EXTSEL_CLK"))
  val vref0 = wordToByteVec(scrfile.control("VREF0"))
  val vref1 = wordToByteVec(scrfile.control("VREF1"))
  val vref2 = wordToByteVec(scrfile.control("VREF2"))
  val iref = wordToByteVec(scrfile.control("IREF"))
  val clkgcal = wordToByteVec(scrfile.control("CLKGCAL"))
  val clkgbias = scrfile.control("CLKGBIAS")

  adc.io.osp0 := osp(0)
  adc.io.osp1 := osp(1)
  adc.io.osp2 := osp(2)
  adc.io.osp3 := osp(3)
  adc.io.osp4 := osp(4)
  adc.io.osp5 := osp(5)
  adc.io.osp6 := osp(6)
  adc.io.osp7 := osp(7)

  adc.io.osm0 := osm(0)
  adc.io.osm1 := osm(1)
  adc.io.osm2 := osm(2)
  adc.io.osm3 := osm(3)
  adc.io.osm4 := osm(4)
  adc.io.osm5 := osm(5)
  adc.io.osm6 := osm(6)
  adc.io.osm7 := osm(7)

  adc.io.extclk0 := io.adcextclk
  adc.io.extclk1 := io.adcextclk
  adc.io.extclk2 := io.adcextclk
  adc.io.extclk3 := io.adcextclk
  adc.io.extclk4 := io.adcextclk
  adc.io.extclk5 := io.adcextclk
  adc.io.extclk6 := io.adcextclk
  adc.io.extclk7 := io.adcextclk

  adc.io.asclkd0 := asclkd(0)
  adc.io.asclkd1 := asclkd(1)
  adc.io.asclkd2 := asclkd(2)
  adc.io.asclkd3 := asclkd(3)
  adc.io.asclkd4 := asclkd(4)
  adc.io.asclkd5 := asclkd(5)
  adc.io.asclkd6 := asclkd(6)
  adc.io.asclkd7 := asclkd(7)

  adc.io.extsel_clk0 := extsel_clk(0)
  adc.io.extsel_clk1 := extsel_clk(1)
  adc.io.extsel_clk2 := extsel_clk(2)
  adc.io.extsel_clk3 := extsel_clk(3)
  adc.io.extsel_clk4 := extsel_clk(4)
  adc.io.extsel_clk5 := extsel_clk(5)
  adc.io.extsel_clk6 := extsel_clk(6)
  adc.io.extsel_clk7 := extsel_clk(7)

  adc.io.vref00 := vref0(0)
  adc.io.vref01 := vref0(1)
  adc.io.vref02 := vref0(2)
  adc.io.vref03 := vref0(3)
  adc.io.vref04 := vref0(4)
  adc.io.vref05 := vref0(5)
  adc.io.vref06 := vref0(6)
  adc.io.vref07 := vref0(7)

  adc.io.vref10 := vref1(0)
  adc.io.vref11 := vref1(1)
  adc.io.vref12 := vref1(2)
  adc.io.vref13 := vref1(3)
  adc.io.vref14 := vref1(4)
  adc.io.vref15 := vref1(5)
  adc.io.vref16 := vref1(6)
  adc.io.vref17 := vref1(7)

  adc.io.vref20 := vref2(0)
  adc.io.vref21 := vref2(1)
  adc.io.vref22 := vref2(2)
  adc.io.vref23 := vref2(3)
  adc.io.vref24 := vref2(4)
  adc.io.vref25 := vref2(5)
  adc.io.vref26 := vref2(6)
  adc.io.vref27 := vref2(7)

  adc.io.iref0 := iref(0)
  adc.io.iref1 := iref(1)
  adc.io.iref2 := iref(2)

  adc.io.clkgcal0 := clkgcal(0)
  adc.io.clkgcal1 := clkgcal(1)
  adc.io.clkgcal2 := clkgcal(2)
  adc.io.clkgcal3 := clkgcal(3)
  adc.io.clkgcal4 := clkgcal(4)
  adc.io.clkgcal5 := clkgcal(5)
  adc.io.clkgcal6 := clkgcal(6)
  adc.io.clkgcal7 := clkgcal(7)

  adc.io.clkgbias := clkgbias

  adc.io.clkrst := io.adcclkrst

  val adcout = Vec(
    adc.io.adcout0,
    adc.io.adcout1,
    adc.io.adcout2,
    adc.io.adcout3,
    adc.io.adcout4,
    adc.io.adcout5,
    adc.io.adcout6,
    adc.io.adcout7)

  val deser = Module(new des72to288)
  deser.io.in := adcout
  deser.io.clk := adc.io.clkout_des
  // [stevo]: wouldn't do anything, since it's only used on reset
  deser.io.phi_init := 0.U
  // unsynchronized ADC clock reset
  deser.io.rst := io.adcclkrst
  
  val des_sync = Vec(deser.io.out.map(s => SyncCrossing(from_clock=deser.io.clkout_data, to_clock=deser.io.clkout_dsp, in=s, sync=1)))
  
  io.adc_clk_out := deser.io.clkout_dsp

  lazy val numInBits = 9
  lazy val numOutBits = 9
  lazy val numSlices = 8*4
  lazy val cal = Module(new ADCCal(numInBits, numOutBits, numSlices))
  cal.io.adcdata := des_sync.asTypeOf(Vec(numSlices, UInt(numInBits.W)))

  cal.io.mode := scrfile.control("MODE")
  cal.io.addr := scrfile.control("ADDR")
  cal.io.wen := scrfile.control("WEN")
  cal.io.calcoeff.zipWithIndex.foreach{ case(port, i) => port := scrfile.control(s"CALCOEFF$i") }
  cal.io.calout.zipWithIndex.foreach{ case(port, i) => scrfile.status(s"CALOUT$i") := port }

  // this lazy weirdness is needed because other traits look at streamIn
  // before this code executes

  lazy val streamIn = Wire(ValidWithSync(cal.io.calout.asTypeOf(UInt())))
  streamIn.bits  := cal.io.calout.asTypeOf(UInt())
  streamIn.valid := scrfile.control("ADC_VALID")
  streamIn.sync  := scrfile.control("ADC_SYNC")

}

class DspChainWithADC(
  b: => Option[DspChainIO with DspChainADCIO] = None,
  override_clock: Option[Clock]=None,
  override_reset: Option[Bool]=None)(implicit p: Parameters) extends 
    DspChain() with LazyADC with LazyCAL {
  lazy val module: DspChainWithADCModule =
    new DspChainWithADCModule(this, b, override_clock, override_reset)
}

class DspChainWithADCModule(
  outer: DspChain,
  b: => Option[DspChainIO with DspChainADCIO] = None,
  override_clock: Option[Clock]=None,
  override_reset: Option[Bool]=None)(implicit p: Parameters)
  extends DspChainModule(outer, b, override_clock, override_reset)
    with ADCModule {
  override lazy val io: DspChainIO with DspChainADCIO = b.getOrElse(new DspChainIO with DspChainADCIO)
}

// [stevo]: copied from rocket-chip, but switched Bool input to Data
object SyncCrossing {
  class SynchronizerBackend[T<:Data](sync: Int, _clock: Clock, gen: T) extends Module(Some(_clock)) {
    val io = IO(new Bundle {
      val in = Input(gen)
      val out = Output(gen)
    })

    io.out := ShiftRegister(io.in, sync)
  }

  class SynchronizerFrontend[T<:Data](_clock: Clock, gen: T) extends Module(Some(_clock)) {
    val io = IO(new Bundle {
      val in = Input(gen)
      val out = Output(gen)
    })

    io.out := RegNext(io.in)
  }

  def apply[T<:Data](from_clock: Clock, to_clock: Clock, in: T, sync: Int = 2): T = {
    val front = Module(new SynchronizerFrontend(from_clock, in.cloneType))
    val back = Module(new SynchronizerBackend(sync, to_clock, in.cloneType))

    front.io.in := in
    back.io.in := front.io.out
    back.io.out
  }
}
