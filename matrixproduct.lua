local function init_array(m, n)
    local mat = {}
    for i = 1, m do
        for j = 1, n do
            table.insert(mat, i + j - 1);
        end
    end

    return mat
end

local function print_first_elems(mat, m, n)
    io.write(tostring(mat[1]))
    for i = 2, math.min(10, n) do
        io.write(", ", tostring(mat[i]))
    end
    print()
end

local function time_func(func)
    local ti = os.clock()
    func()
    local tf = os.clock()
    return (tf - ti) * 1000  -- ms
end

local function on_mult(m, n, p)
    local temp
    
    local mat_a = init_array(m, n)
    local mat_b = init_array(n, p)
    local mat_c = {}

    for i = 0, m - 1 do
        for j = 0, p - 1 do
            temp = 0
            for k = 0, n - 1 do
                temp = temp + mat_a[i * n + k + 1] * mat_b[k * p + j + 1];
            end
            table.insert(mat_c, temp)
        end
    end

    print("Result matrix:")
    print_first_elems(mat_c, n, p);
end

local function on_mult_line(m, n, p)
    error("Not implemented")
end

local function on_mult_block(m, n, p)
    error("Not implemented")
end

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

    local time
    if op == 1 then
        time = time_func(function () on_mult(line, col, line) end)
    elseif op == 2 then
        time = time_func(function () on_mult_line(line, col, line) end)
    elseif op == 3 then
        time = time_func(function () on_mult_block(line, col, line) end)
    else
        print_usage()
        return
    end

    print("Time:", time, "ms")
end

main()