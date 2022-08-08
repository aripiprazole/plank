/*
 *    Copyright 2022 Plank
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.plank.llvm4k.ir

import llvm.LLVMCallConv

public actual enum class CallingConv(public val llvm: LLVMCallConv) {
  /**
   * C - The default llvm calling convention, compatible with C.  This
   * convention is the only calling convention that supports varargs calls.
   * As with typical C calling conventions, the callee/caller have to
   * tolerate certain amounts of prototype mismatch.
   */
  C(LLVMCallConv.LLVMCCallConv),

  /**
   *  Generic LLVM calling conventions.  None of these calling conventions
   *  support varargs calls, and all assume that the caller and callee
   *  prototype exactly match.
   *
   *  Fast - This calling convention attempts to make calls as fast as
   *  possible (e.g. by passing things in registers).
   */
  Fast(LLVMCallConv.LLVMFastCallConv),

  /**
   * Cold - This calling convention attempts to make code in the caller as
   * efficient as possible under the assumption that the call is not commonly
   * executed.  As such, these calls often preserve all registers so that the
   * call does not break any live ranges in the caller side.
   */
  Cold(LLVMCallConv.LLVMColdCallConv),

  /**
   * GHC - Calling convention used by the Glasgow Haskell Compiler (GHC).
   */
  GHC(LLVMCallConv.LLVMGHCCallConv),

  /**
   * HiPE - Calling convention used by the High-Performance Erlang Compiler
   * (HiPE).
   */
  HiPE(LLVMCallConv.LLVMHiPECallConv),

  /**
   * WebKit JS - Calling convention for stack based JavaScript calls
   */
  WebKit_JS(LLVMCallConv.LLVMWebKitJSCallConv),

  /**
   * AnyReg - Calling convention for dynamic register based calls (e.g.
   * stackmap and patchpoint intrinsics).
   */
  AnyReg(LLVMCallConv.LLVMAnyRegCallConv),

  /**
   * PreserveMost - Calling convention for runtime calls that preserves most
   * registers.
   */
  PreserveMost(LLVMCallConv.LLVMPreserveMostCallConv),

  /**
   * PreserveAll - Calling convention for runtime calls that preserves
   * (almost) all registers.
   */
  PreserveAll(LLVMCallConv.LLVMPreserveAllCallConv),

  /**
   * Swift - Calling convention for Swift.
   */
  Swift(LLVMCallConv.LLVMSwiftCallConv),

  /**
   *CXX_FAST_TLS - Calling convention for access functions.
   */
  CXX_FAST_TLS(LLVMCallConv.LLVMCXXFASTTLSCallConv),

  /**
   * X86_StdCall - stdcall is the calling conventions mostly used by the
   * Win32 API. It is basically the same as the C convention with the
   * difference in that the callee is responsible for popping the arguments
   * from the stack.
   */
  X86_StdCall(LLVMCallConv.LLVMX86StdcallCallConv),

  /**
   * X86_FastCall - 'fast' analog of X86_StdCall. Passes first two arguments
   * in ECX:EDX registers, others - via stack. Callee is responsible for
   * stack cleaning.
   */
  X86_FastCall(LLVMCallConv.LLVMX86FastcallCallConv),

  /**
   * ARM_APCS - ARM Procedure Calling Standard calling convention (obsolete,
   * but still used on some targets).
   */
  ARM_APCS(LLVMCallConv.LLVMARMAPCSCallConv),

  /**
   * ARM_AAPCS - ARM Architecture Procedure Calling Standard calling
   * convention (aka EABI). Soft float variant.
   */
  ARM_AAPCS(LLVMCallConv.LLVMARMAAPCSCallConv),

  /**
   * ARM_AAPCS_VFP - Same as ARM_AAPCS, but uses hard floating point ABI.
   */
  ARM_AAPCS_VFP(LLVMCallConv.LLVMARMAAPCSVFPCallConv),

  /**
   * MSP430_INTR - Calling convention used for MSP430 interrupt routines.
   */
  MSP430_INTR(LLVMCallConv.LLVMMSP430INTRCallConv),

  /**
   * X86_ThisCall - Similar to X86_StdCall. Passes first argument in ECX,
   * others via stack. Callee is responsible for stack cleaning. MSVC uses
   * this by default for methods in its ABI.
   */
  X86_ThisCall(LLVMCallConv.LLVMX86ThisCallCallConv),

  /**
   * PTX_Kernel - Call to a PTX kernel.
   * Passes all arguments in parameter space.
   */
  PTX_Kernel(LLVMCallConv.LLVMPTXKernelCallConv),

  /**
   * PTX_Device - Call to a PTX device function.
   * Passes all arguments in register or parameter space.
   */
  PTX_Device(LLVMCallConv.LLVMPTXDeviceCallConv),

  /**
   * SPIR_FUNC - Calling convention for SPIR non-kernel device functions.
   * No lowering or expansion of arguments.
   * Structures are passed as a pointer to a struct with the byval attribute.
   * Functions can only call SPIR_FUNC and SPIR_KERNEL functions.
   * Functions can only have zero or one return values.
   * Variable arguments are not allowed, except for printf.
   * How arguments/return values are lowered are not specified.
   * Functions are only visible to the devices.
   */
  SPIR_FUNC(LLVMCallConv.LLVMSPIRFUNCCallConv),

  /**
   * SPIR_KERNEL - Calling convention for SPIR kernel functions.
   * Inherits the restrictions of SPIR_FUNC, except
   * Cannot have non-void return values.
   * Cannot have variable arguments.
   * Can also be called by the host.
   * Is externally visible.
   */
  SPIR_KERNEL(LLVMCallConv.LLVMSPIRKERNELCallConv),

  /**
   * Intel_OCL_BI - Calling conventions for Intel OpenCL built-ins
   */
  Intel_OCL_BI(LLVMCallConv.LLVMIntelOCLBICallConv),

  /**
   * The C convention as specified in the x86-64 supplement to the
   * System V ABI, used on most non-Windows systems.
   */
  X86_64_SysV(LLVMCallConv.LLVMX8664SysVCallConv),

  /**
   * The C convention as implemented on Windows/x86-64 and
   * AArch64. This convention differs from the more common
   * \c X86_64_SysV convention in a number of ways, most notably in
   * that XMM registers used to pass arguments are shadowed by GPRs,
   * and vice versa.
   * On AArch64, this is identical to the normal C (AAPCS) calling
   * convention for normal functions, but floats are passed in integer
   * registers to variadic functions.
   */
  Win64(LLVMCallConv.LLVMWin64CallConv),

  /**
   * MSVC calling convention that passes vectors and vector aggregates
   * in SSE registers.
   */
  X86_VectorCall(LLVMCallConv.LLVMX86VectorCallCallConv),

  /**
   * Calling convention used by HipHop Virtual Machine (HHVM) to
   * perform calls to and from translation cache, and for calling PHP
   * functions.
   * HHVM calling convention supports tail/sibling call elimination.
   */
  HHVM(LLVMCallConv.LLVMHHVMCallConv),

  /**
   * HHVM calling convention for invoking C/C++ helpers.
   */
  HHVM_C(LLVMCallConv.LLVMHHVMCCallConv),

  /**
   * X86_INTR - x86 hardware interrupt context. Callee may take one or two
   * parameters, where the 1st represents a pointer to hardware context frame
   * and the 2nd represents hardware error code, the presence of the later
   * depends on the interrupt vector taken. Valid for both 32- and 64-bit
   * subtargets.
   */
  X86_INTR(LLVMCallConv.LLVMX86INTRCallConv),

  /**
   * Used for AVR interrupt routines.
   */
  AVR_INTR(LLVMCallConv.LLVMAVRINTRCallConv),

  /**
   * Calling convention used for AVR signal routines.
   */
  AVR_SIGNAL(LLVMCallConv.LLVMAVRSIGNALCallConv),

  /**
   * Calling convention used for special AVR rtlib functions
   * which have an "optimized" convention to preserve registers.
   */
  AVR_BUILTIN(LLVMCallConv.LLVMAVRBUILTINCallConv),

  /**
   * Calling convention used for Mesa vertex shaders, or AMDPAL last shader
   * stage before rasterization (vertex shader if tessellation and geometry
   * are not in use, or otherwise copy shader if one is needed).
   */
  AMDGPU_VS(LLVMCallConv.LLVMAMDGPUVSCallConv),

  /**
   * Calling convention used for Mesa/AMDPAL geometry shaders.
   */
  AMDGPU_GS(LLVMCallConv.LLVMAMDGPUGSCallConv),

  /**
   * Calling convention used for Mesa/AMDPAL pixel shaders.
   */
  AMDGPU_PS(LLVMCallConv.LLVMAMDGPUPSCallConv),

  /**
   * Calling convention used for Mesa/AMDPAL compute shaders.
   */
  AMDGPU_CS(LLVMCallConv.LLVMAMDGPUCSCallConv),

  /**
   * Calling convention for AMDGPU code object kernels.
   */
  AMDGPU_KERNEL(LLVMCallConv.LLVMAMDGPUKERNELCallConv),

  /**
   * Register calling convention used for parameters transfer optimization
   */
  X86_RegCall(LLVMCallConv.LLVMX86RegCallCallConv),

  /**
   * Calling convention used for Mesa/AMDPAL hull shaders (= tessellation
   * control shaders).
   */
  AMDGPU_HS(LLVMCallConv.LLVMAMDGPUHSCallConv),

  /**
   * Calling convention used for special MSP430 rtlib functions
   * which have an "optimized" convention using additional registers.
   */
  MSP430_BUILTIN(LLVMCallConv.LLVMMSP430BUILTINCallConv),

  /**
   * Calling convention used for AMDPAL vertex shader if tessellation is in
   * use.
   */
  AMDGPU_LS(LLVMCallConv.LLVMAMDGPULSCallConv),

  /**
   * Calling convention used for AMDPAL shader stage before geometry shader
   * if geometry is in use. So either the domain (= tessellation evaluation)
   * shader if tessellation is in use, or otherwise the vertex shader.
   */
  AMDGPU_ES(LLVMCallConv.LLVMAMDGPUESCallConv);

  public actual val value: UInt get() = llvm.value

  public actual companion object {
    public fun byValue(llvm: LLVMCallConv): CallingConv {
      return byValue(llvm.value)
    }

    public actual fun byValue(value: Int): CallingConv {
      return byValue(value.toUInt())
    }

    public actual fun byValue(value: UInt): CallingConv {
      return values().single { it.llvm.value == value }
    }
  }
}
