local function print_usage()
    print("Usage: lua matrixproduct.lua <op> <lin> <col> <output> [blockSize]")
    print("  <op>       : Opration mode: 1, 2, 3 (required)")
    print("  <lin>      : Number of lines (required)")
    print("  <col>      : Number of columns (required)")
    print("  <output>   : Path to output filename (required)")
    print("  [blockSize]: Size of a block (optional)")
end

local function main()
    if #arg < 4 or #arg > 5 then
        print_usage()
        return
    end

    local op = tonumber(arg[1]);
    if op ~= 3 and #arg > 4 then
        print_usage()
        return
    end

    local line = math.tointeger(arg[2])
    local col = math.tointeger(arg[3])
    local filename = arg[4]
    local block_size = tonumber(arg[5]);

    if line == nil or col == nil or (op == 3 and block_size == nil) then
        print_usage()
        return
    end    
end

main()