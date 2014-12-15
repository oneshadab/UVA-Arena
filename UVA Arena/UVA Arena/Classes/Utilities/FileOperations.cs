﻿using System;
using System.Runtime.InteropServices;

public class SHOptionsAPI
{
    /// <summary>
    /// Possible flags for the SHFileOperation method.
    /// </summary>
    [Flags]
    public enum FileOperationFlags : ushort
    {
        /// <summary>
        /// Do not show a dialog during the process
        /// </summary>
        FOF_SILENT = 0x0004,
        /// <summary>
        /// Do not ask the user to confirm selection
        /// </summary>
        FOF_NOCONFIRMATION = 0x0010,
        /// <summary>
        /// Delete the file to the recycle bin.  (Required flag to send a file to the bin
        /// </summary>
        FOF_ALLOWUNDO = 0x0040,
        /// <summary>
        /// Do not show the names of the files or folders that are being recycled.
        /// </summary>
        FOF_SIMPLEPROGRESS = 0x0100,
        /// <summary>
        /// Surpress errors, if any occur during the process.
        /// </summary>
        FOF_NOERRORUI = 0x0400,
        /// <summary>
        /// Warn if files are too big to fit in the recycle bin and will need
        /// to be deleted completely.
        /// </summary>
        FOF_WANTNUKEWARNING = 0x4000,
    }

    /// <summary>
    /// File Operation Function Type for SHFileOperation
    /// </summary>
    public enum FileOperationType : uint
    {
        /// <summary>
        /// Move the objects
        /// </summary>
        FO_MOVE = 0x0001,
        /// <summary>
        /// Copy the objects
        /// </summary>
        FO_COPY = 0x0002,
        /// <summary>
        /// Delete (or recycle) the objects
        /// </summary>
        FO_DELETE = 0x0003,
        /// <summary>
        /// Rename the object(s)
        /// </summary>
        FO_RENAME = 0x0004,
    }

    /// <summary>
    /// SHFILEOPSTRUCT for SHFileOperation from COM
    /// </summary>
    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto, Pack = 1)]
    private struct SHFILEOPSTRUCT
    {

        public IntPtr hwnd;
        [MarshalAs(UnmanagedType.U4)]
        public FileOperationType wFunc;
        public string pFrom;
        public string pTo;
        public FileOperationFlags fFlags;
        [MarshalAs(UnmanagedType.Bool)]
        public bool fAnyOperationsAborted;
        public IntPtr hNameMappings;
        public string lpszProgressTitle;
    }

    [DllImport("shell32.dll", CharSet = CharSet.Auto)]
    private static extern int SHFileOperation(ref SHFILEOPSTRUCT FileOp);

    public static void CallSH(FileOperationType type, string[] from, string to, FileOperationFlags flags)
    {
        try
        {
            var fs = new SHFILEOPSTRUCT
            {
                wFunc = type,
                pFrom = string.Join("\0", from) + "\0\0",
                pTo = to + "\0\0",
                fFlags = flags
            };
            SHFileOperation(ref fs);
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public static void Rename(string from, string to)
    {
        CallSH(FileOperationType.FO_RENAME, new string[] { from }, to, FileOperationFlags.FOF_ALLOWUNDO);
    }

    public static void Copy(string[] from, string to)
    {
        CallSH(FileOperationType.FO_COPY, from, to, FileOperationFlags.FOF_SILENT);
    }

    public static void Move(string[] from, string to)
    {
        CallSH(FileOperationType.FO_MOVE, from, to, FileOperationFlags.FOF_SILENT);
    }

    public static void Delete(string[] paths, FileOperationFlags flags)
    {
        CallSH(FileOperationType.FO_DELETE, paths, "", flags);
    }

    public static void SendToRecycleBin(string[] paths)
    {
        Delete(paths, FileOperationFlags.FOF_ALLOWUNDO | FileOperationFlags.FOF_WANTNUKEWARNING);
    }
}
