def get_overlap(a_str, b_str):
    equal = []
    for a, b in zip(a_str, b_str):
        if a == b:
            equal.append(a)
        else:
            break
    return ''.join(equal)


def add_string_to_prefix_trie(node, s):
    if s == '':
        return
    found = False
    for prefix in [p for p in node.keys() if isinstance(p, str)]:
        if prefix.startswith(s[0]):
            found = True
            if s == prefix:
                # Ensure node is a terminating node
                node[prefix][1] = True
                return
            shared = get_overlap(s, prefix)
            if len(shared) == len(prefix):
                # No need to split node, fully overlapping prefix
                add_string_to_prefix_trie(node[shared], s[len(shared):])
            else:
                # Split the node, partially shared
                node[shared] = {prefix[len(shared):]: node[prefix], s[len(shared):]: {1: True}}
                del node[prefix]
            break
    if not found:
        node[s] = {1: True}


def build_prefix_trie(strings):
    root = {}
    for s in strings:
        add_string_to_prefix_trie(root, s)
    return root


# def trie_contains(trie, s):
#     pass


if __name__ == '__main__':
    print(build_prefix_trie(["ab", "abcd", "abcd"], {}))
    print(build_prefix_trie(["ab", "abcd", "abce"], {}))
