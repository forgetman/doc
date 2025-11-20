def process_txt_file(file_path):
    """
    处理TXT文件，按规则查找目标行并按B行分组归类（支持多A行对应同一B行）
    :param file_path: TXT文件的路径
    :return: 按B行分组的结果列表，每个元素为字典：{'b_line': str, 'c_line': str, 'a_lines': list}
    """
    # 配置目标字段
    target_a = "www.hsbc.co.id/1"  # A行需要包含的字段
    target_b_include = "mobile/"   # B行需要包含的字段
    target_b_exclude = "ios"       # B行需要排除的字段
    # 核心分组字典：键=B行索引，值=包含B行、C行、A行列表的字典
    b_group_dict = {}
    lines = []                     # 存储文件的所有行（保留原始格式）

    # 1. 读取TXT文件内容（兼容UTF-8/GBK编码）
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
        if not lines:
            print("错误：文件为空！")
            return []
    except FileNotFoundError:
        print(f"错误：未找到文件 {file_path}，请检查路径是否正确")
        return []
    except UnicodeDecodeError:
        print("错误：文件编码非UTF-8，尝试使用GBK编码读取...")
        with open(file_path, 'r', encoding='gbk') as f:
            lines = f.readlines()
    except Exception as e:
        print(f"读取文件时发生未知错误：{str(e)}")
        return []

    # 2. 查找所有包含target_a的A行（记录行索引和行内容）
    a_line_list = [(idx, line) for idx, line in enumerate(lines) if target_a in line]
    if not a_line_list:
        print("未找到包含 'www.hsbc.co.id/1' 的A行")
        return []

    # 3. 遍历每个A行，向前查找B行并按B行索引分组
    for a_idx, a_line in a_line_list:
        b_idx = None
        # 从A行上一行向前遍历，找到第一个符合条件的B行
        for idx in range(a_idx - 1, -1, -1):
            current_line = lines[idx]
            if target_b_include in current_line and target_b_exclude not in current_line:
                b_idx = idx
                break  # 找到最近的B行后停止查找

        if b_idx is None:
            print(f"A行（行号：{a_idx + 1}）前未找到符合条件的B行，跳过该A行")
            continue

        # 4. 检查B行是否有对应的C行（B行下一行）
        c_idx = b_idx + 1
        if c_idx >= len(lines):
            print(f"B行（行号：{b_idx + 1}）是文件最后一行，无C行，跳过该组")
            continue
        b_line = lines[b_idx]
        c_line = lines[c_idx]

        # 5. 按B行索引分组：同一B行的A行添加到同一组的a_lines列表中
        if b_idx not in b_group_dict:
            # 首次找到该B行，初始化分组
            b_group_dict[b_idx] = {
                'b_line': b_line,
                'c_line': c_line,
                'a_lines': [a_line]  # 初始化A行列表
            }
        else:
            # 该B行已存在，追加A行到列表
            b_group_dict[b_idx]['a_lines'].append(a_line)

    # 6. 将分组字典转换为有序列表（按B行出现的先后顺序）
    result_list = [b_group_dict[key] for key in sorted(b_group_dict.keys())]
    return result_list

def generate_output_content(group_result):
    """
    根据分组结果生成控制台打印和文件写入的统一内容
    :param group_result: 分组后的结果列表
    :return: 格式化的输出字符串
    """
    content = []
    content.append("=" * 80)
    content.append(f"共找到 {len(group_result)} 组有效数据（按仓库文件路径分组）")
    content.append("=" * 80)

    for group_num, group in enumerate(group_result, 1):
        content.append(f"第{group_num}组：")
        content.append(f"仓库名字：{group['b_line'].strip()}")
        content.append(f"文件路径：{group['c_line'].strip()}")
        # 打印该组下的所有A行（按A1/A2/A3...编号）
        for a_num, a_line in enumerate(group['a_lines'], 1):
            content.append(f"文本{a_num}: {a_line.strip()}")
        content.append("=" * 80)  # 分隔线

    # 将列表拼接为字符串，每行用换行符分隔
    return "\n".join(content)

def save_to_txt(content, save_path):
    """
    将格式化的内容写入TXT文件
    :param content: 要写入的字符串内容
    :param save_path: 保存文件的路径
    """
    try:
        with open(save_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"\n结果已成功保存到文件：{save_path}")
    except PermissionError:
        print(f"错误：无权限写入文件 {save_path}，请检查文件是否被占用或路径权限")
    except FileNotFoundError:
        print(f"错误：保存路径不存在 {save_path}，请检查路径是否正确")
    except Exception as e:
        print(f"写入文件时发生未知错误：{str(e)}")

# 主程序执行
if __name__ == "__main__":
    # 请替换为你的TXT文件实际路径
    txt_file_path = "huifeng.txt"  # 输入文件路径：示例 r"C:\test\input.txt"
    # 请替换为结果保存的文件路径
    save_file_path = "result_output.txt"  # 输出文件路径：示例 r"C:\test\output.txt"

    # 1. 处理文件获取分组结果
    group_result = process_txt_file(txt_file_path)

    # 2. 生成统一的格式化内容
    output_content = generate_output_content(group_result)

    # 3. 控制台打印结果
    print(output_content)

    # 4. 将结果写入TXT文件
    if group_result:  # 仅当有有效结果时才写入文件
        save_to_txt(output_content, save_file_path)
    else:
        print("\n无有效结果，无需写入文件")