#!/bin/bash
# 實時查看應用程序錯誤日誌

echo "正在監控錯誤日誌... (按 Ctrl+C 停止)"
echo "=========================================="
tail -f log/blog-dev.log | grep --line-buffered -i "error\|exception\|500" --color=always
