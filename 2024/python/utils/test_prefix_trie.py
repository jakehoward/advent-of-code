import pytest
from utils.prefix_trie import build_prefix_trie, add_string_to_prefix_trie


# Function because pytest seems to mutate things...
def t():
    return {1: True}  # terminating node


class TestPrefixTrie:
    def test_build_prefix_trie(self):
        assert ({'ab': t()} == build_prefix_trie(["ab"]))
        assert ({'ab': {**t(), 'cd': t()}} == build_prefix_trie(["ab", "abcd"]))
        assert ({'ab': {**t(), 'cd': t()}} == build_prefix_trie(["ab", "abcd", "abcd"]))

    def test_split_node(self):
        node = {'ab': {**t(), 'cd': t()}}
        add_string_to_prefix_trie(node, 'abce')
        assert ({'ab': {**t(), 'c': {'d': t(), 'e': t()}}} == node)

    def test_split_node_with_children(self):
        node = {'ab': {**t(), 'cd': {**t(), 'ef': t()}}}
        add_string_to_prefix_trie(node, 'abce')
        assert ({'ab': {**t(), 'c': {'e': t(), 'd': {**t(), 'ef': t()}}}} == node)

    def test_add_children(self):
        node = {'ab': {**t(), 'cd': t()}}
        add_string_to_prefix_trie(node, 'abcdef')
        assert ({'ab': {**t(), 'cd': {**t(), 'ef': t()}}} == node)

    def test_add_terminating_node_to_existing_node(self):
        node = {'ab': {**t(), 'c': {'d': t(), 'e': t()}}}
        add_string_to_prefix_trie(node, 'abc')
        assert({'ab': {**t(), 'c': {**t(), 'd': t(), 'e': t()}}} == node)

    # def test_query_prefix_tree(self):
    #     pt = build_prefix_trie(["Jake", "Loves", "Clojure"], t())
    #     assert(True == trie_contains(pt, "Jake"))
    #     assert(True == trie_contains(pt, "Loves"))
    #     assert(True == trie_contains(pt, "Clojure"))
    #     assert(False == trie_contains(pt, "Ja"))
    #     assert(False == trie_contains(pt, "ke"))
    #     assert(False == trie_contains(pt, "e"))
    #     assert(False == trie_contains(pt, "J"))
    #     assert(False == trie_contains(pt, "Cloj"))

