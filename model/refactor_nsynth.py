import os
import json

def delete_all_except_acoustic_guitar(dir):
    file_list = [f for f in os.listdir(dir) if f.startswith("guitar_acoustic") is False]
    for f in file_list:
        os.remove(os.path.join(dir,f))

def delete_from_json_all_except_acoustic_guitar(src_path,dest_path):
    with open(dest_path, 'w') as dest_file:
        with open(src_path, 'r') as source_file:
            examples = json.loads(source_file.read())        
            for e in list(examples):
                if(e.startswith('guitar_acoustic') is False):  
                    del examples[e]   
            dest_file.write(json.dumps(examples))

def merge_sets(train_path, valid_path,test_path,dest_path):
    with open(train_path, 'r') as train:
        with open(valid_path, 'r') as valid:
            with open(test_path, 'r') as test: 
                with open(dest_path, 'w') as dest_file:
                    result = { 
                        **json.loads(train.read()),
                        **json.loads(valid.read()),
                        **json.loads(test.read())
                    }
                    result = dict(sorted(result.items()))
                    print(len(result))
                    dest_file.write(json.dumps(result))

delete_all_except_acoustic_guitar('./nsynth-train/audio')
delete_all_except_acoustic_guitar('./nsynth-valid/audio')
delete_all_except_acoustic_guitar('./nsynth-test/audio')

delete_from_json_all_except_acoustic_guitar('./nsynth-train/examples.json','./nsynth-train/new-examples.json')
delete_from_json_all_except_acoustic_guitar('./nsynth-valid/examples.json','./nsynth-valid/new-examples.json')
delete_from_json_all_except_acoustic_guitar('./nsynth-test/examples.json','./nsynth-test/new-examples.json')

merge_sets('./nsynth-train/new-examples.json', './nsynth-valid/new-examples.json','./nsynth-test/new-examples.json','./new-examples.json')